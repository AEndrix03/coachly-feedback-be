package it.aredegalli.coachly.feedback.model;

import it.aredegalli.coachly.feedback.model.AuditableEntity;
import it.aredegalli.coachly.feedback.model.ModerationReportStatus;
import it.aredegalli.coachly.feedback.model.TargetType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "moderation_reports")
public class ModerationReport extends AuditableEntity {

    @Column(nullable = false)
    private UUID reporterUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TargetType targetType;

    @Column(nullable = false)
    private UUID targetId;

    @Column(nullable = false, length = 100)
    private String reason;

    @Column(length = 2000)
    private String details;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModerationReportStatus status = ModerationReportStatus.OPEN;

    @Column
    private Instant resolvedAt;

    @Column
    private UUID resolvedByUserId;

    public UUID getReporterUserId() { return reporterUserId; }
    public void setReporterUserId(UUID reporterUserId) { this.reporterUserId = reporterUserId; }
    public TargetType getTargetType() { return targetType; }
    public void setTargetType(TargetType targetType) { this.targetType = targetType; }
    public UUID getTargetId() { return targetId; }
    public void setTargetId(UUID targetId) { this.targetId = targetId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public ModerationReportStatus getStatus() { return status; }
    public void setStatus(ModerationReportStatus status) { this.status = status; }
    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }
    public UUID getResolvedByUserId() { return resolvedByUserId; }
    public void setResolvedByUserId(UUID resolvedByUserId) { this.resolvedByUserId = resolvedByUserId; }
}


