package it.aredegalli.coachly.feedback.service.impl;

import it.aredegalli.coachly.feedback.exception.NotFoundException;
import it.aredegalli.coachly.feedback.model.DomainEventPublisher;
import it.aredegalli.coachly.feedback.model.FeatureRequestSort;
import it.aredegalli.coachly.feedback.model.FeatureRequestStatus;
import it.aredegalli.coachly.feedback.model.StatusHistoryTargetType;
import it.aredegalli.coachly.feedback.model.VoteType;
import it.aredegalli.coachly.feedback.dto.PagedResponse;
import it.aredegalli.coachly.feedback.security.AuthorizationService;
import it.aredegalli.coachly.feedback.security.RequestUserContext;
import it.aredegalli.coachly.feedback.security.RequestUserContextResolver;
import it.aredegalli.coachly.feedback.time.TimeProvider;
import it.aredegalli.coachly.feedback.util.PageRequestFactory;
import it.aredegalli.coachly.feedback.controller.request.ChangeFeatureStatusRequest;
import it.aredegalli.coachly.feedback.controller.request.CreateFeatureRequestRequest;
import it.aredegalli.coachly.feedback.dto.FeatureRequestResponse;
import it.aredegalli.coachly.feedback.controller.request.UpdateFeatureRequestRequest;
import it.aredegalli.coachly.feedback.model.FeatureRankingStrategy;
import it.aredegalli.coachly.feedback.model.FeatureRequest;
import it.aredegalli.coachly.feedback.model.FeatureRequestCreatedEvent;
import it.aredegalli.coachly.feedback.model.FeatureRequestPolicy;
import it.aredegalli.coachly.feedback.model.FeatureRequestVotedEvent;
import it.aredegalli.coachly.feedback.model.FeatureVote;
import it.aredegalli.coachly.feedback.repository.FeatureRequestRepository;
import it.aredegalli.coachly.feedback.repository.FeatureVoteRepository;
import it.aredegalli.coachly.feedback.mapper.FeatureRequestMapper;
import it.aredegalli.coachly.feedback.repository.StatusHistoryRepository;
import it.aredegalli.coachly.feedback.model.StatusHistory;
import it.aredegalli.coachly.feedback.service.FeatureRequestService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class FeatureRequestServiceImpl implements FeatureRequestService {

    private final FeatureRequestRepository featureRequestRepository;
    private final FeatureVoteRepository featureVoteRepository;
    private final RequestUserContextResolver contextResolver;
    private final AuthorizationService authorizationService;
    private final FeatureRequestPolicy policy;
    private final StatusHistoryRepository statusHistoryRepository;
    private final TimeProvider timeProvider;
    private final FeatureRankingStrategy rankingStrategy;
    private final DomainEventPublisher eventPublisher;

    public FeatureRequestServiceImpl(FeatureRequestRepository featureRequestRepository,
                                 FeatureVoteRepository featureVoteRepository,
                                 RequestUserContextResolver contextResolver,
                                 AuthorizationService authorizationService,
                                 FeatureRequestPolicy policy,
                                 StatusHistoryRepository statusHistoryRepository,
                                 TimeProvider timeProvider,
                                 FeatureRankingStrategy rankingStrategy,
                                 DomainEventPublisher eventPublisher) {
        this.featureRequestRepository = featureRequestRepository;
        this.featureVoteRepository = featureVoteRepository;
        this.contextResolver = contextResolver;
        this.authorizationService = authorizationService;
        this.policy = policy;
        this.statusHistoryRepository = statusHistoryRepository;
        this.timeProvider = timeProvider;
        this.rankingStrategy = rankingStrategy;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public FeatureRequestResponse create(CreateFeatureRequestRequest request) {
        RequestUserContext context = contextResolver.getRequiredContext();
        FeatureRequest entity = new FeatureRequest();
        entity.setAuthorUserId(context.userId());
        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setCategory(request.category());
        entity.setPlatformTarget(request.platformTarget());
        entity.setModuleKey(request.moduleKey());
        FeatureRequest saved = featureRequestRepository.save(entity);
        eventPublisher.publish(new FeatureRequestCreatedEvent(saved.getId(), saved.getAuthorUserId()));
        return FeatureRequestMapper.toResponse(saved);
    }

    @Transactional
    public FeatureRequestResponse update(UUID id, UpdateFeatureRequestRequest request) {
        RequestUserContext context = contextResolver.getRequiredContext();
        FeatureRequest entity = findEntity(id);
        authorizationService.requireOwnerOrModerator(context, entity.getAuthorUserId());

        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setCategory(request.category());
        entity.setPlatformTarget(request.platformTarget());
        entity.setModuleKey(request.moduleKey());
        return FeatureRequestMapper.toResponse(featureRequestRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public FeatureRequestResponse getById(UUID id) {
        return FeatureRequestMapper.toResponse(findEntity(id));
    }

    @Transactional(readOnly = true)
    public PagedResponse<FeatureRequestResponse> list(String status,
                                                      String category,
                                                      String search,
                                                      String sort,
                                                      String moduleKey,
                                                      String platformTarget,
                                                      int page,
                                                      int size) {
        FeatureRequestSort sortType = FeatureRequestSort.from(sort);
        if (sortType == FeatureRequestSort.TRENDING) {
            var spec = FeatureRequestRepository.hasFilters(status, category, search, moduleKey, platformTarget);
            List<FeatureRequestResponse> responses = featureRequestRepository.findAll(spec)
                    .stream()
                    .sorted(Comparator.comparingDouble((FeatureRequest fr) ->
                            rankingStrategy.trendScore(fr.getUpvotesCount(), fr.getCommentsCount(), fr.getCreatedAt())).reversed())
                    .skip((long) page * size)
                    .limit(size)
                    .map(FeatureRequestMapper::toResponse)
                    .toList();
            long total = featureRequestRepository.count(spec);
            int totalPages = (int) Math.ceil((double) total / size);
            return new PagedResponse<>(responses, page, size, total, totalPages, page == 0, page >= totalPages - 1);
        }

        Sort springSort = switch (sortType) {
            case NEWEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case OLDEST -> Sort.by(Sort.Direction.ASC, "createdAt");
            case TOP -> Sort.by(Sort.Direction.DESC, "upvotesCount");
            case DISCUSSED -> Sort.by(Sort.Direction.DESC, "commentsCount");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };

        var spec = FeatureRequestRepository.hasFilters(status, category, search, moduleKey, platformTarget);
        var pageResult = featureRequestRepository.findAll(spec, PageRequest.of(page, size, springSort)).map(FeatureRequestMapper::toResponse);
        return PagedResponse.from(pageResult);
    }

    @Transactional
    public FeatureRequestResponse vote(UUID id, VoteType voteType) {
        RequestUserContext context = contextResolver.getRequiredContext();
        FeatureRequest request = findEntity(id);
        policy.validateVoteAllowed(request.getStatus(), voteType);

        FeatureVote vote = featureVoteRepository.findByFeatureRequestIdAndUserId(id, context.userId())
                .orElseGet(() -> {
                    FeatureVote v = new FeatureVote();
                    v.setFeatureRequestId(id);
                    v.setUserId(context.userId());
                    return v;
                });
        vote.setVoteType(voteType);
        featureVoteRepository.save(vote);
        request.setUpvotesCount((int) featureVoteRepository.countByFeatureRequestId(id));
        featureRequestRepository.save(request);
        eventPublisher.publish(new FeatureRequestVotedEvent(id, context.userId()));
        return FeatureRequestMapper.toResponse(request);
    }

    @Transactional
    public FeatureRequestResponse removeVote(UUID id) {
        RequestUserContext context = contextResolver.getRequiredContext();
        FeatureRequest request = findEntity(id);
        featureVoteRepository.findByFeatureRequestIdAndUserId(id, context.userId()).ifPresent(featureVoteRepository::delete);
        request.setUpvotesCount((int) featureVoteRepository.countByFeatureRequestId(id));
        return FeatureRequestMapper.toResponse(featureRequestRepository.save(request));
    }

    @Transactional
    public FeatureRequestResponse changeStatus(UUID id, ChangeFeatureStatusRequest request) {
        RequestUserContext context = contextResolver.getRequiredContext();
        authorizationService.requireAdmin(context);
        FeatureRequest entity = findEntity(id);

        FeatureRequestStatus oldStatus = entity.getStatus();
        entity.setStatus(request.status());
        featureRequestRepository.save(entity);

        StatusHistory statusHistory = new StatusHistory();
        statusHistory.setTargetType(StatusHistoryTargetType.FEATURE_REQUEST);
        statusHistory.setTargetId(id);
        statusHistory.setOldStatus(oldStatus.name());
        statusHistory.setNewStatus(request.status().name());
        statusHistory.setChangedByUserId(context.userId());
        statusHistory.setPublicNote(request.publicNote());
        statusHistory.setInternalNote(request.internalNote());
        statusHistory.setChangedAt(timeProvider.now());
        statusHistoryRepository.save(statusHistory);

        return FeatureRequestMapper.toResponse(entity);
    }

    @Transactional
    public FeatureRequestResponse markDuplicate(UUID id, UUID masterId) {
        RequestUserContext context = contextResolver.getRequiredContext();
        authorizationService.requireAdmin(context);
        findEntity(masterId);
        FeatureRequest entity = findEntity(id);
        entity.setDuplicateOfId(masterId);
        entity.setStatus(FeatureRequestStatus.DUPLICATE);
        return FeatureRequestMapper.toResponse(featureRequestRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<StatusHistory> history(UUID id) {
        return statusHistoryRepository.findByTargetTypeAndTargetIdOrderByChangedAtDesc(StatusHistoryTargetType.FEATURE_REQUEST, id);
    }

    private FeatureRequest findEntity(UUID id) {
        return featureRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("FEATURE_REQUEST_NOT_FOUND", "Feature request not found"));
    }
}



