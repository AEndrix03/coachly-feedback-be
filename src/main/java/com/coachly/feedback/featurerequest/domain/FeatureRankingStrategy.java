package com.coachly.feedback.featurerequest.domain;

import java.time.Instant;

public interface FeatureRankingStrategy {
    double trendScore(int upvotesCount, int commentsCount, Instant createdAt);
}