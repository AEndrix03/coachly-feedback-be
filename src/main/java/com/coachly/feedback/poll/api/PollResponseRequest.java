package com.coachly.feedback.poll.api;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record PollResponseRequest(@NotEmpty List<UUID> optionIds) {
}