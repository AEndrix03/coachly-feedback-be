package it.aredegalli.coachly.feedback.service;

import java.util.List;
import java.util.Map;

public interface AnalyticsService {
    Map<String, Object> overview();
    List<Map<String, Object>> topFeatureRequests();
    List<Map<String, Object>> ratingsByTarget();
    List<Map<String, Object>> pollsEngagement();
}

