package it.aredegalli.coachly.feedback.service.impl;

import it.aredegalli.coachly.feedback.exception.NotFoundException;
import it.aredegalli.coachly.feedback.model.FeedbackType;
import it.aredegalli.coachly.feedback.model.TargetType;
import it.aredegalli.coachly.feedback.dto.PagedResponse;
import it.aredegalli.coachly.feedback.security.AuthorizationService;
import it.aredegalli.coachly.feedback.security.RequestUserContext;
import it.aredegalli.coachly.feedback.security.RequestUserContextResolver;
import it.aredegalli.coachly.feedback.util.PageRequestFactory;
import it.aredegalli.coachly.feedback.controller.request.CreateFeedbackRequest;
import it.aredegalli.coachly.feedback.dto.FeedbackResponse;
import it.aredegalli.coachly.feedback.dto.FeedbackSummaryResponse;
import it.aredegalli.coachly.feedback.controller.request.UpdateFeedbackRequest;
import it.aredegalli.coachly.feedback.model.FeedbackEntry;
import it.aredegalli.coachly.feedback.model.FeedbackPolicy;
import it.aredegalli.coachly.feedback.repository.FeedbackEntryRepository;
import it.aredegalli.coachly.feedback.mapper.FeedbackMapper;
import it.aredegalli.coachly.feedback.service.FeedbackService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackEntryRepository repository;
    private final FeedbackPolicy policy;
    private final RequestUserContextResolver contextResolver;
    private final AuthorizationService authorizationService;

    public FeedbackServiceImpl(FeedbackEntryRepository repository,
                           FeedbackPolicy policy,
                           RequestUserContextResolver contextResolver,
                           AuthorizationService authorizationService) {
        this.repository = repository;
        this.policy = policy;
        this.contextResolver = contextResolver;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public FeedbackResponse create(CreateFeedbackRequest request) {
        RequestUserContext context = contextResolver.getRequiredContext();
        policy.validateForType(request.type(), request.ratingValue(), request.severity());

        if (request.type() == FeedbackType.REVIEW) {
            repository.findByAuthorUserIdAndTypeAndTargetTypeAndTargetId(context.userId(), FeedbackType.REVIEW, request.targetType(), request.targetId())
                    .ifPresent(existing -> {
                        existing.setTitle(request.title());
                        existing.setBody(request.body());
                        existing.setRatingValue(request.ratingValue());
                        existing.setCategory(request.category());
                        existing.setFeatureKey(request.featureKey());
                        existing.setScreenKey(request.screenKey());
                        existing.setFlowKey(request.flowKey());
                        existing.setPlatform(request.platform());
                        existing.setAppVersion(request.appVersion());
                        repository.save(existing);
                    });
            var existing = repository.findByAuthorUserIdAndTypeAndTargetTypeAndTargetId(context.userId(), FeedbackType.REVIEW, request.targetType(), request.targetId());
            if (existing.isPresent()) {
                return FeedbackMapper.toResponse(existing.get());
            }
        }

        FeedbackEntry entry = new FeedbackEntry();
        entry.setAuthorUserId(context.userId());
        entry.setType(request.type());
        entry.setTitle(request.title());
        entry.setBody(request.body());
        entry.setRatingValue(request.ratingValue());
        entry.setCategory(request.category());
        entry.setTargetType(request.targetType());
        entry.setTargetId(request.targetId());
        entry.setFeatureKey(request.featureKey());
        entry.setScreenKey(request.screenKey());
        entry.setFlowKey(request.flowKey());
        entry.setPlatform(request.platform());
        entry.setAppVersion(request.appVersion());
        entry.setSeverity(request.severity());
        entry.setReproducible(request.reproducible());
        return FeedbackMapper.toResponse(repository.save(entry));
    }

    @Transactional
    public FeedbackResponse update(UUID id, UpdateFeedbackRequest request) {
        RequestUserContext context = contextResolver.getRequiredContext();
        FeedbackEntry entry = repository.findById(id).orElseThrow(() -> new NotFoundException("FEEDBACK_NOT_FOUND", "Feedback entry not found"));
        authorizationService.requireOwnerOrModerator(context, entry.getAuthorUserId());
        policy.validateForType(entry.getType(), request.ratingValue(), request.severity());

        entry.setTitle(request.title());
        entry.setBody(request.body());
        entry.setRatingValue(request.ratingValue());
        entry.setCategory(request.category());
        entry.setFeatureKey(request.featureKey());
        entry.setScreenKey(request.screenKey());
        entry.setFlowKey(request.flowKey());
        entry.setPlatform(request.platform());
        entry.setAppVersion(request.appVersion());
        entry.setSeverity(request.severity());
        entry.setReproducible(request.reproducible());

        return FeedbackMapper.toResponse(repository.save(entry));
    }

    @Transactional(readOnly = true)
    public FeedbackResponse getById(UUID id) {
        return FeedbackMapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("FEEDBACK_NOT_FOUND", "Feedback entry not found")));
    }

    @Transactional(readOnly = true)
    public PagedResponse<FeedbackResponse> list(String type,
                                                String targetType,
                                                UUID targetId,
                                                String category,
                                                UUID authorUserId,
                                                String status,
                                                int page,
                                                int size,
                                                String sort) {
        var pageable = PageRequestFactory.of(page, size, sort == null ? "createdAt" : sort, Sort.Direction.DESC);
        var specification = FeedbackEntryRepository.hasFilters(type, targetType, targetId, category, authorUserId, status);
        return PagedResponse.from(repository.findAll(specification, pageable).map(FeedbackMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public FeedbackSummaryResponse summary(TargetType targetType, UUID targetId) {
        Double avg = repository.averageRating(targetType, targetId);
        long total = repository.reviewsCount(targetType, targetId);
        Map<Integer, Long> distribution = new HashMap<>();
        repository.ratingDistribution(targetType, targetId)
                .forEach(row -> distribution.put((Integer) row[0], (Long) row[1]));
        return new FeedbackSummaryResponse(targetType, targetId, avg == null ? 0.0 : avg, total, distribution);
    }
}



