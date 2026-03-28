package com.coachly.feedback.poll.domain;

import com.coachly.feedback.common.model.AuditableEntity;
import com.coachly.feedback.common.model.PollStatus;
import com.coachly.feedback.common.model.PollType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "polls")
public class Poll extends AuditableEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PollType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PollStatus status = PollStatus.DRAFT;

    @Column(nullable = false)
    private boolean multipleChoice;

    @Column(nullable = false)
    private boolean anonymousResults;

    @Column(nullable = false)
    private Instant opensAt;

    @Column(nullable = false)
    private Instant closesAt;

    @Column(nullable = false)
    private UUID createdByUserId;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public PollType getType() { return type; }
    public void setType(PollType type) { this.type = type; }
    public PollStatus getStatus() { return status; }
    public void setStatus(PollStatus status) { this.status = status; }
    public boolean isMultipleChoice() { return multipleChoice; }
    public void setMultipleChoice(boolean multipleChoice) { this.multipleChoice = multipleChoice; }
    public boolean isAnonymousResults() { return anonymousResults; }
    public void setAnonymousResults(boolean anonymousResults) { this.anonymousResults = anonymousResults; }
    public Instant getOpensAt() { return opensAt; }
    public void setOpensAt(Instant opensAt) { this.opensAt = opensAt; }
    public Instant getClosesAt() { return closesAt; }
    public void setClosesAt(Instant closesAt) { this.closesAt = closesAt; }
    public UUID getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(UUID createdByUserId) { this.createdByUserId = createdByUserId; }
}