package it.aredegalli.coachly.feedback.service;

import it.aredegalli.coachly.feedback.dto.FeedbackResponse;
import it.aredegalli.coachly.feedback.dto.FeedbackSummaryResponse;
import it.aredegalli.coachly.feedback.dto.PagedResponse;
import it.aredegalli.coachly.feedback.controller.request.CreateFeedbackRequest;
import it.aredegalli.coachly.feedback.controller.request.UpdateFeedbackRequest;
import it.aredegalli.coachly.feedback.model.TargetType;

import java.util.UUID;

public interface FeedbackService {
    FeedbackResponse create(CreateFeedbackRequest request);
    FeedbackResponse update(UUID id, UpdateFeedbackRequest request);
    FeedbackResponse getById(UUID id);
    PagedResponse<FeedbackResponse> list(String type, String targetType, UUID targetId, String category, UUID authorUserId, String status, int page, int size, String sort);
    FeedbackSummaryResponse summary(TargetType targetType, UUID targetId);
}

