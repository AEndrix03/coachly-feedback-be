package it.aredegalli.coachly.feedback.controller;

import it.aredegalli.coachly.feedback.dto.*;
import it.aredegalli.coachly.feedback.dto.ApiResponse;
import it.aredegalli.coachly.feedback.model.TargetType;
import it.aredegalli.coachly.feedback.dto.PagedResponse;
import it.aredegalli.coachly.feedback.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feedback")
public class FeedbackController {

    private final FeedbackService service;

    public FeedbackController(FeedbackService service) {
        this.service = service;
    }

    @PostMapping
    public ApiResponse<FeedbackResponse> create(@Valid @RequestBody CreateFeedbackRequest request) {
        return ApiResponse.of(service.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<FeedbackResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateFeedbackRequest request) {
        return ApiResponse.of(service.update(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<FeedbackResponse> getById(@PathVariable UUID id) {
        return ApiResponse.of(service.getById(id));
    }

    @GetMapping
    public ApiResponse<PagedResponse<FeedbackResponse>> list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) UUID targetId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) UUID authorUserId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort
    ) {
        return ApiResponse.of(service.list(type, targetType, targetId, category, authorUserId, status, page, size, sort));
    }

    @GetMapping("/summary")
    public ApiResponse<FeedbackSummaryResponse> summary(@RequestParam TargetType targetType, @RequestParam UUID targetId) {
        return ApiResponse.of(service.summary(targetType, targetId));
    }
}






