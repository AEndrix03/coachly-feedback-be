package it.aredegalli.coachly.feedback.dto;

import it.aredegalli.coachly.feedback.model.ModerationReportStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateModerationReportStatusRequest(@NotNull ModerationReportStatus status) {
}


