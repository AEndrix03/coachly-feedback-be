package com.coachly.feedback.poll.domain;

import java.util.UUID;

public record PollAnsweredEvent(UUID pollId, UUID userId) {
}