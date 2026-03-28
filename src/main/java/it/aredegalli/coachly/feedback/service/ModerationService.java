package it.aredegalli.coachly.feedback.service;

import it.aredegalli.coachly.feedback.dto.CreateModerationReportRequest;
import it.aredegalli.coachly.feedback.model.ModerationReport;
import it.aredegalli.coachly.feedback.model.ModerationReportStatus;
import it.aredegalli.coachly.feedback.model.TargetType;

import java.util.List;
import java.util.UUID;

public interface ModerationService {
    ModerationReport create(CreateModerationReportRequest request);
    List<ModerationReport> list();
    ModerationReport updateStatus(UUID reportId, ModerationReportStatus status);
    void hide(TargetType targetType, UUID targetId);
    void restore(TargetType targetType, UUID targetId);
}

