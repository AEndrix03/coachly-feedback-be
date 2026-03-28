package it.aredegalli.coachly.feedback.model;

import it.aredegalli.coachly.feedback.model.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "poll_response_options")
public class PollResponseOption extends AuditableEntity {

    @Column(nullable = false)
    private UUID pollResponseId;

    @Column(nullable = false)
    private UUID pollOptionId;

    public UUID getPollResponseId() { return pollResponseId; }
    public void setPollResponseId(UUID pollResponseId) { this.pollResponseId = pollResponseId; }
    public UUID getPollOptionId() { return pollOptionId; }
    public void setPollOptionId(UUID pollOptionId) { this.pollOptionId = pollOptionId; }
}


