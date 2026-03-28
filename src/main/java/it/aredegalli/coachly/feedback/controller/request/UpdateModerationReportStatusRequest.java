package it.aredegalli.coachly.feedback.controller.request;

import it.aredegalli.coachly.feedback.model.ModerationReportStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateModerationReportStatusRequest(@NotNull ModerationReportStatus status) {
}



