package it.aredegalli.coachly.feedback.model;

import java.util.UUID;

public record PollAnsweredEvent(UUID pollId, UUID userId) {
}


