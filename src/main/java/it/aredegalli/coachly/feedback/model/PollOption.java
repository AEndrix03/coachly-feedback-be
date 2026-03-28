package it.aredegalli.coachly.feedback.model;

import it.aredegalli.coachly.feedback.model.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "poll_options")
public class PollOption extends AuditableEntity {

    @Column(nullable = false)
    private UUID pollId;

    @Column(nullable = false, length = 500)
    private String label;

    @Column(nullable = false)
    private int position;

    public UUID getPollId() { return pollId; }
    public void setPollId(UUID pollId) { this.pollId = pollId; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}


