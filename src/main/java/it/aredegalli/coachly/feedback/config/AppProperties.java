package it.aredegalli.coachly.feedback.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        SecurityHeaders security,
        Comments comments,
        Features features,
        Polls polls,
        Ranking ranking
) {
    public record SecurityHeaders(String userId, String userRole, String correlationId) {}
    public record Comments(int maxDepth, List<String> allowedVoteTypes) {}
    public record Features(List<String> votingEnabledStatuses, boolean publicRoadmapVisible) {}
    public record Polls(boolean allowAnonymousResults) {}
    public record Ranking(double agePenaltyPerDay) {}
}


