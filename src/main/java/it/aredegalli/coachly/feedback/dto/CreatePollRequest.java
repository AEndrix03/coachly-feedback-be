package it.aredegalli.coachly.feedback.dto;

import it.aredegalli.coachly.feedback.model.PollType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public record CreatePollRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 4000) String description,
        @NotNull PollType type,
        boolean multipleChoice,
        boolean anonymousResults,
        @NotNull Instant opensAt,
        @NotNull Instant closesAt,
        @NotEmpty List<@NotBlank @Size(max = 500) String> options
) {
}


