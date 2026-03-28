package com.coachly.feedback.comment.application;

import com.coachly.feedback.comment.api.CommentResponse;
import com.coachly.feedback.comment.api.CreateCommentRequest;
import com.coachly.feedback.comment.api.UpdateCommentRequest;
import com.coachly.feedback.comment.domain.Comment;
import com.coachly.feedback.comment.domain.CommentCreatedEvent;
import com.coachly.feedback.comment.domain.CommentPolicy;
import com.coachly.feedback.comment.domain.CommentVote;
import com.coachly.feedback.comment.infrastructure.CommentRepository;
import com.coachly.feedback.comment.infrastructure.CommentVoteRepository;
import com.coachly.feedback.comment.mapper.CommentMapper;
import com.coachly.feedback.common.exception.BadRequestException;
import com.coachly.feedback.common.exception.NotFoundException;
import com.coachly.feedback.common.model.CommentStatus;
import com.coachly.feedback.common.model.DomainEventPublisher;
import com.coachly.feedback.common.model.TargetType;
import com.coachly.feedback.common.model.VoteType;
import com.coachly.feedback.common.security.AuthorizationService;
import com.coachly.feedback.common.security.RequestUserContext;
import com.coachly.feedback.common.security.RequestUserContextResolver;
import com.coachly.feedback.common.time.TimeProvider;
import com.coachly.feedback.featurerequest.domain.FeatureRequest;
import com.coachly.feedback.featurerequest.infrastructure.FeatureRequestRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentVoteRepository commentVoteRepository;
    private final CommentPolicy policy;
    private final RequestUserContextResolver contextResolver;
    private final AuthorizationService authorizationService;
    private final TimeProvider timeProvider;
    private final FeatureRequestRepository featureRequestRepository;
    private final DomainEventPublisher eventPublisher;

    public CommentService(CommentRepository commentRepository,
                          CommentVoteRepository commentVoteRepository,
                          CommentPolicy policy,
                          RequestUserContextResolver contextResolver,
                          AuthorizationService authorizationService,
                          TimeProvider timeProvider,
                          FeatureRequestRepository featureRequestRepository,
                          DomainEventPublisher eventPublisher) {
        this.commentRepository = commentRepository;
        this.commentVoteRepository = commentVoteRepository;
        this.policy = policy;
        this.contextResolver = contextResolver;
        this.authorizationService = authorizationService;
        this.timeProvider = timeProvider;
        this.featureRequestRepository = featureRequestRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public CommentResponse create(CreateCommentRequest request) {
        RequestUserContext context = contextResolver.getRequiredContext();

        int depth = 0;
        UUID rootId = null;
        if (request.parentCommentId() != null) {
            Comment parent = findComment(request.parentCommentId());
            if (!parent.getTargetType().equals(request.targetType()) || !parent.getTargetId().equals(request.targetId())) {
                throw new BadRequestException("COMMENT_TARGET_MISMATCH", "Reply target must match parent target");
            }
            depth = parent.getDepth() + 1;
            policy.validateDepth(depth);
            rootId = parent.getRootCommentId() == null ? parent.getId() : parent.getRootCommentId();
            parent.setRepliesCount(parent.getRepliesCount() + 1);
            commentRepository.save(parent);
        }

        Comment comment = new Comment();
        comment.setTargetType(request.targetType());
        comment.setTargetId(request.targetId());
        comment.setAuthorUserId(context.userId());
        comment.setParentCommentId(request.parentCommentId());
        comment.setRootCommentId(rootId);
        comment.setDepth(depth);
        comment.setBody(request.body());

        Comment saved = commentRepository.save(comment);
        incrementTargetCommentCounter(saved.getTargetType(), saved.getTargetId());
        eventPublisher.publish(new CommentCreatedEvent(saved.getId(), saved.getTargetId()));
        return CommentMapper.toResponse(saved);
    }

    @Transactional
    public CommentResponse update(UUID id, UpdateCommentRequest request) {
        RequestUserContext context = contextResolver.getRequiredContext();
        Comment comment = findComment(id);
        if (comment.getStatus() == CommentStatus.DELETED) {
            throw new BadRequestException("COMMENT_DELETED", "Deleted comments cannot be edited");
        }
        authorizationService.requireOwnerOrModerator(context, comment.getAuthorUserId());
        comment.setBody(request.body());
        return CommentMapper.toResponse(commentRepository.save(comment));
    }

    @Transactional
    public void softDelete(UUID id) {
        RequestUserContext context = contextResolver.getRequiredContext();
        Comment comment = findComment(id);
        authorizationService.requireOwnerOrModerator(context, comment.getAuthorUserId());
        comment.setStatus(CommentStatus.DELETED);
        comment.setBody("[deleted]");
        comment.setDeletedAt(timeProvider.now());
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> list(TargetType targetType, UUID targetId, UUID parentId, String sort) {
        Sort springSort = switch ((sort == null ? "top" : sort).toLowerCase()) {
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "score").and(Sort.by(Sort.Direction.DESC, "createdAt"));
        };

        List<Comment> comments = parentId == null
                ? commentRepository.findByTargetTypeAndTargetIdAndParentCommentIdIsNullAndDeletedAtIsNull(targetType, targetId, springSort)
                : commentRepository.findByTargetTypeAndTargetIdAndParentCommentIdAndDeletedAtIsNull(targetType, targetId, parentId, springSort);
        return comments.stream().map(CommentMapper::toResponse).toList();
    }

    @Transactional
    public CommentResponse vote(UUID commentId, VoteType voteType) {
        RequestUserContext context = contextResolver.getRequiredContext();
        Comment comment = findComment(commentId);

        CommentVote vote = commentVoteRepository.findByCommentIdAndUserId(commentId, context.userId())
                .orElseGet(() -> {
                    CommentVote v = new CommentVote();
                    v.setCommentId(commentId);
                    v.setUserId(context.userId());
                    return v;
                });
        vote.setVoteType(voteType);
        commentVoteRepository.save(vote);
        recalculateScore(comment);
        return CommentMapper.toResponse(commentRepository.save(comment));
    }

    @Transactional
    public CommentResponse removeVote(UUID commentId) {
        RequestUserContext context = contextResolver.getRequiredContext();
        Comment comment = findComment(commentId);
        commentVoteRepository.findByCommentIdAndUserId(commentId, context.userId()).ifPresent(commentVoteRepository::delete);
        recalculateScore(comment);
        return CommentMapper.toResponse(commentRepository.save(comment));
    }

    private Comment findComment(UUID id) {
        return commentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("COMMENT_NOT_FOUND", "Comment not found"));
    }

    private void recalculateScore(Comment comment) {
        List<CommentVote> votes = commentVoteRepository.findAll().stream()
                .filter(v -> v.getCommentId().equals(comment.getId()))
                .toList();
        int score = 0;
        for (CommentVote vote : votes) {
            if (vote.getVoteType() == VoteType.UP || vote.getVoteType() == VoteType.LIKE) {
                score += 1;
            } else if (vote.getVoteType() == VoteType.DOWN) {
                score -= 1;
            }
        }
        comment.setScore(score);
    }

    private void incrementTargetCommentCounter(TargetType targetType, UUID targetId) {
        if (targetType == TargetType.FEATURE_REQUEST) {
            FeatureRequest request = featureRequestRepository.findById(targetId)
                    .orElseThrow(() -> new NotFoundException("FEATURE_REQUEST_NOT_FOUND", "Feature request not found for comment target"));
            request.setCommentsCount((int) commentRepository.countByTargetTypeAndTargetIdAndDeletedAtIsNull(TargetType.FEATURE_REQUEST, targetId));
            featureRequestRepository.save(request);
        }
    }
}