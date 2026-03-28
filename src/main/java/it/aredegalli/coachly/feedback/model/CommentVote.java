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
@Table(name = "comment_votes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_comment_vote_comment_user", columnNames = {"comment_id", "user_id"})
})
public class CommentVote extends AuditableEntity {

    @Column(name = "comment_id", nullable = false)
    private UUID commentId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private VoteType voteType;

    public UUID getCommentId() { return commentId; }
    public void setCommentId(UUID commentId) { this.commentId = commentId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public VoteType getVoteType() { return voteType; }
    public void setVoteType(VoteType voteType) { this.voteType = voteType; }
}


