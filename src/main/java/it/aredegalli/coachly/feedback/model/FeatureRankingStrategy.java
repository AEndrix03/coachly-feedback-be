package it.aredegalli.coachly.feedback.model;

import java.time.Instant;

public interface FeatureRankingStrategy {
    double trendScore(int upvotesCount, int commentsCount, Instant createdAt);
}


