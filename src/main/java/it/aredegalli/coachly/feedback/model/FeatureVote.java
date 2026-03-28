package it.aredegalli.coachly.feedback.model;

import it.aredegalli.coachly.feedback.model.AuditableEntity;
import it.aredegalli.coachly.feedback.model.VoteType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

@Entity
@Table(name = "feature_votes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_feature_vote_feature_user", columnNames = {"feature_request_id", "user_id"})
})
public class FeatureVote extends AuditableEntity {

    @Column(name = "feature_request_id", nullable = false)
    private UUID featureRequestId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private VoteType voteType;

    public UUID getFeatureRequestId() { return featureRequestId; }
    public void setFeatureRequestId(UUID featureRequestId) { this.featureRequestId = featureRequestId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public VoteType getVoteType() { return voteType; }
    public void setVoteType(VoteType voteType) { this.voteType = voteType; }
}


