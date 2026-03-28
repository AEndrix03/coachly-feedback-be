package it.aredegalli.coachly.feedback.dto;

import it.aredegalli.coachly.feedback.model.TargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateCommentRequest(
        @NotNull TargetType targetType,
        @NotNull UUID targetId,
        UUID parentCommentId,
        @NotBlank @Size(max = 3000) String body
) {
}


