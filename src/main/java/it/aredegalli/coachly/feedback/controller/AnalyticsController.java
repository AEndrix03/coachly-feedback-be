package it.aredegalli.coachly.feedback.controller;

import it.aredegalli.coachly.feedback.dto.ApiResponse;
import it.aredegalli.coachly.feedback.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/analytics")
public class AnalyticsController {

    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> overview() {
        return ApiResponse.of(service.overview());
    }

    @GetMapping("/feature-requests/top")
    public ApiResponse<List<Map<String, Object>>> topFeatureRequests() {
        return ApiResponse.of(service.topFeatureRequests());
    }

    @GetMapping("/ratings/by-target")
    public ApiResponse<List<Map<String, Object>>> ratingsByTarget() {
        return ApiResponse.of(service.ratingsByTarget());
    }

    @GetMapping("/polls/engagement")
    public ApiResponse<List<Map<String, Object>>> pollsEngagement() {
        return ApiResponse.of(service.pollsEngagement());
    }
}

