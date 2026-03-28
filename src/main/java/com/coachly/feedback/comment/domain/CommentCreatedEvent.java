package com.coachly.feedback.comment.domain;

import java.util.UUID;

public record CommentCreatedEvent(UUID commentId, UUID targetId) {
}