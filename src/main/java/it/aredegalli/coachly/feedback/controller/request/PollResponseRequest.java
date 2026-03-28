package it.aredegalli.coachly.feedback.controller.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record PollResponseRequest(@NotEmpty List<UUID> optionIds) {
}



