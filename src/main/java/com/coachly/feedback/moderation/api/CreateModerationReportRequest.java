package com.coachly.feedback.moderation.api;

import com.coachly.feedback.common.model.TargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateModerationReportRequest(
        @NotNull TargetType targetType,
        @NotNull UUID targetId,
        @NotBlank @Size(max = 100) String reason,
        @Size(max = 2000) String details
) {
}