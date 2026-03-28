package com.coachly.feedback.comment.api;

import com.coachly.feedback.common.model.CommentStatus;
import com.coachly.feedback.common.model.TargetType;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        TargetType targetType,
        UUID targetId,
        UUID authorUserId,
        UUID parentCommentId,
        UUID rootCommentId,
        int depth,
        String body,
        CommentStatus status,
        int score,
        int repliesCount,
        Instant createdAt,
        Instant updatedAt
) {
}