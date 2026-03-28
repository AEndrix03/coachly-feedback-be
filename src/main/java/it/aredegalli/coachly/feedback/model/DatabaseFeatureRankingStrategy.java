package it.aredegalli.coachly.feedback.model;

import it.aredegalli.coachly.feedback.config.AppProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class DatabaseFeatureRankingStrategy implements FeatureRankingStrategy {

    private final AppProperties properties;

    public DatabaseFeatureRankingStrategy(AppProperties properties) {
        this.properties = properties;
    }

    @Override
    public double trendScore(int upvotesCount, int commentsCount, Instant createdAt) {
        double ageDays = Duration.between(createdAt, Instant.now()).toHours() / 24.0;
        return upvotesCount * 3.0 + commentsCount * 2.0 - ageDays * properties.ranking().agePenaltyPerDay();
    }
}


