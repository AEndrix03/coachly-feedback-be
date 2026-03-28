package com.coachly.feedback.moderation.api;

import com.coachly.feedback.common.model.ModerationReportStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateModerationReportStatusRequest(@NotNull ModerationReportStatus status) {
}