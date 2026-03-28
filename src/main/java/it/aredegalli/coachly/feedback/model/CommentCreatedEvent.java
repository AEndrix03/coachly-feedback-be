package it.aredegalli.coachly.feedback.model;

import java.util.UUID;

public record CommentCreatedEvent(UUID commentId, UUID targetId) {
}


