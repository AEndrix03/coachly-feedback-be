package it.aredegalli.coachly.feedback.dto;

import it.aredegalli.coachly.feedback.model.CommentStatus;
import it.aredegalli.coachly.feedback.model.TargetType;

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


