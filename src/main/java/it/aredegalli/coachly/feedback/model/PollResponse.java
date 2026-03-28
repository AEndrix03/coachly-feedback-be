package it.aredegalli.coachly.feedback.model;

import it.aredegalli.coachly.feedback.model.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "poll_responses", uniqueConstraints = {
        @UniqueConstraint(name = "uk_poll_response_poll_user", columnNames = {"poll_id", "user_id"})
})
public class PollResponse extends AuditableEntity {

    @Column(nullable = false)
    private UUID pollId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private Instant submittedAt;

    public UUID getPollId() { return pollId; }
    public void setPollId(UUID pollId) { this.pollId = pollId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public Instant getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }
}


