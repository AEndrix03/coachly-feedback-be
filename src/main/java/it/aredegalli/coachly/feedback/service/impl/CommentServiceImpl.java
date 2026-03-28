package it.aredegalli.coachly.feedback.service.impl;

import it.aredegalli.coachly.feedback.dto.CommentResponse;
import it.aredegalli.coachly.feedback.dto.CreateCommentRequest;
import it.aredegalli.coachly.feedback.dto.UpdateCommentRequest;
import it.aredegalli.coachly.feedback.model.Comment;
import it.aredegalli.coachly.feedback.model.CommentCreatedEvent;
import it.aredegalli.coachly.feedback.model.CommentPolicy;
import it.aredegalli.coachly.feedback.model.CommentVote;
import it.aredegalli.coachly.feedback.repository.CommentRepository;
import it.aredegalli.coachly.feedback.repository.CommentVoteRepository;
import it.aredegalli.coachly.feedback.mapper.CommentMapper;
import it.aredegalli.coachly.feedback.exception.BadRequestException;
import it.aredegalli.coachly.feedback.exception.NotFoundException;
import it.aredegalli.coachly.feedback.model.CommentStatus;
import it.aredegalli.coachly.feedback.model.DomainEventPublisher;
import it.aredegalli.coachly.feedback.model.TargetType;
import it.aredegalli.coachly.feedback.model.VoteType;
import it.aredegalli.coachly.feedback.security.AuthorizationService;
import it.aredegalli.coachly.feedback.security.RequestUserContext;
import it.aredegalli.coachly.feedback.security.RequestUserContextResolver;
import it.aredegalli.coachly.feedback.time.TimeProvider;
import it.aredegalli.coachly.feedback.model.FeatureRequest;
import it.aredegalli.coachly.feedback.repository.FeatureRequestRepository;
import it.aredegalli.coachly.feedback.service.CommentService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentVoteRepository commentVoteRepository;
    private final CommentPolicy policy;
    private final RequestUserContextResolver contextResolver;
    private final AuthorizationService authorizationService;
    private final TimeProvider timeProvider;
    private final FeatureRequestRepository featureRequestRepository;
    private final DomainEventPublisher eventPublisher;

    public CommentServiceImpl(CommentRepository commentRepository,
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



