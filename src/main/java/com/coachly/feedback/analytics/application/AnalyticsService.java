package com.coachly.feedback.analytics.application;

import com.coachly.feedback.common.security.AuthorizationService;
import com.coachly.feedback.common.security.RequestUserContextResolver;
import com.coachly.feedback.comment.infrastructure.CommentRepository;
import com.coachly.feedback.feedback.infrastructure.FeedbackEntryRepository;
import com.coachly.feedback.featurerequest.infrastructure.FeatureRequestRepository;
import com.coachly.feedback.moderation.infrastructure.ModerationReportRepository;
import com.coachly.feedback.poll.infrastructure.PollRepository;
import com.coachly.feedback.poll.infrastructure.PollResponseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final RequestUserContextResolver contextResolver;
    private final AuthorizationService authorizationService;
    private final FeedbackEntryRepository feedbackRepository;
    private final FeatureRequestRepository featureRequestRepository;
    private final PollRepository pollRepository;
    private final CommentRepository commentRepository;
    private final ModerationReportRepository moderationReportRepository;
    private final PollResponseRepository pollResponseRepository;

    public AnalyticsService(RequestUserContextResolver contextResolver,
                            AuthorizationService authorizationService,
                            FeedbackEntryRepository feedbackRepository,
                            FeatureRequestRepository featureRequestRepository,
                            PollRepository pollRepository,
                            CommentRepository commentRepository,
                            ModerationReportRepository moderationReportRepository,
                            PollResponseRepository pollResponseRepository) {
        this.contextResolver = contextResolver;
        this.authorizationService = authorizationService;
        this.feedbackRepository = feedbackRepository;
        this.featureRequestRepository = featureRequestRepository;
        this.pollRepository = pollRepository;
        this.commentRepository = commentRepository;
        this.moderationReportRepository = moderationReportRepository;
        this.pollResponseRepository = pollResponseRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> overview() {
        authorizationService.requireModeratorOrAdmin(contextResolver.getRequiredContext());
        Map<String, Object> out = new HashMap<>();
        out.put("feedbackCount", feedbackRepository.count());
        out.put("featureRequestCount", featureRequestRepository.count());
        out.put("pollCount", pollRepository.count());
        out.put("commentCount", commentRepository.count());
        out.put("openReports", moderationReportRepository.count());
        return out;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> topFeatureRequests() {
        authorizationService.requireModeratorOrAdmin(contextResolver.getRequiredContext());
        return featureRequestRepository.findAll().stream()
                .sorted((a, b) -> Integer.compare(b.getUpvotesCount(), a.getUpvotesCount()))
                .limit(10)
                .map(feature -> Map.<String, Object>of(
                        "id", feature.getId(),
                        "title", feature.getTitle(),
                        "upvotes", feature.getUpvotesCount(),
                        "comments", feature.getCommentsCount()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> ratingsByTarget() {
        authorizationService.requireModeratorOrAdmin(contextResolver.getRequiredContext());
        return feedbackRepository.findAll().stream()
                .filter(f -> f.getRatingValue() != null)
                .map(feedback -> Map.<String, Object>of(
                        "targetType", feedback.getTargetType(),
                        "targetId", feedback.getTargetId(),
                        "rating", feedback.getRatingValue(),
                        "category", feedback.getCategory()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> pollsEngagement() {
        authorizationService.requireModeratorOrAdmin(contextResolver.getRequiredContext());
        return pollRepository.findAll().stream()
                .map(poll -> Map.<String, Object>of(
                        "pollId", poll.getId(),
                        "title", poll.getTitle(),
                        "responses", pollResponseRepository.countByPollId(poll.getId())))
                .toList();
    }
}