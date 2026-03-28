package com.coachly.feedback.feedback.application;

import com.coachly.feedback.common.exception.NotFoundException;
import com.coachly.feedback.common.model.FeedbackType;
import com.coachly.feedback.common.model.TargetType;
import com.coachly.feedback.common.pagination.PagedResponse;
import com.coachly.feedback.common.security.AuthorizationService;
import com.coachly.feedback.common.security.RequestUserContext;
import com.coachly.feedback.common.security.RequestUserContextResolver;
import com.coachly.feedback.common.util.PageRequestFactory;
import com.coachly.feedback.feedback.api.CreateFeedbackRequest;
import com.coachly.feedback.feedback.api.FeedbackResponse;
import com.coachly.feedback.feedback.api.FeedbackSummaryResponse;
import com.coachly.feedback.feedback.api.UpdateFeedbackRequest;
import com.coachly.feedback.feedback.domain.FeedbackEntry;
import com.coachly.feedback.feedback.domain.FeedbackPolicy;
import com.coachly.feedback.feedback.infrastructure.FeedbackEntryRepository;
import com.coachly.feedback.feedback.mapper.FeedbackMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FeedbackService {

    private final FeedbackEntryRepository repository;
    private final FeedbackPolicy policy;
    private final RequestUserContextResolver contextResolver;
    private final AuthorizationService authorizationService;

    public FeedbackService(FeedbackEntryRepository repository,
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