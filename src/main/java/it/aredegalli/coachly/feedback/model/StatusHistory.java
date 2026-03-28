package it.aredegalli.coachly.feedback.model;

import it.aredegalli.coachly.feedback.model.AuditableEntity;
import it.aredegalli.coachly.feedback.model.StatusHistoryTargetType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "status_history")
public class StatusHistory extends AuditableEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusHistoryTargetType targetType;

    @Column(nullable = false)
    private UUID targetId;

    @Column(nullable = false, length = 30)
    private String oldStatus;

    @Column(nullable = false, length = 30)
    private String newStatus;

    @Column(nullable = false)
    private UUID changedByUserId;

    @Column(length = 1000)
    private String publicNote;

    @Column(length = 1000)
    private String internalNote;

    @Column(nullable = false)
    private Instant changedAt;

    public StatusHistoryTargetType getTargetType() { return targetType; }
    public void setTargetType(StatusHistoryTargetType targetType) { this.targetType = targetType; }
    public UUID getTargetId() { return targetId; }
    public void setTargetId(UUID targetId) { this.targetId = targetId; }
    public String getOldStatus() { return oldStatus; }
    public void setOldStatus(String oldStatus) { this.oldStatus = oldStatus; }
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    public UUID getChangedByUserId() { return changedByUserId; }
    public void setChangedByUserId(UUID changedByUserId) { this.changedByUserId = changedByUserId; }
    public String getPublicNote() { return publicNote; }
    public void setPublicNote(String publicNote) { this.publicNote = publicNote; }
    public String getInternalNote() { return internalNote; }
    public void setInternalNote(String internalNote) { this.internalNote = internalNote; }
    public Instant getChangedAt() { return changedAt; }
    public void setChangedAt(Instant changedAt) { this.changedAt = changedAt; }
}


