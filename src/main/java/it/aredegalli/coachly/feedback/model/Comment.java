package it.aredegalli.coachly.feedback.model;

import it.aredegalli.coachly.feedback.model.AuditableEntity;
import it.aredegalli.coachly.feedback.model.CommentStatus;
import it.aredegalli.coachly.feedback.model.TargetType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "comments")
public class Comment extends AuditableEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TargetType targetType;

    @Column(nullable = false)
    private UUID targetId;

    @Column(nullable = false)
    private UUID authorUserId;

    @Column
    private UUID parentCommentId;

    @Column
    private UUID rootCommentId;

    @Column(nullable = false)
    private int depth;

    @Column(nullable = false, length = 3000)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CommentStatus status = CommentStatus.ACTIVE;

    @Column(nullable = false)
    private int score = 0;

    @Column(nullable = false)
    private int repliesCount = 0;

    public TargetType getTargetType() { return targetType; }
    public void setTargetType(TargetType targetType) { this.targetType = targetType; }
    public UUID getTargetId() { return targetId; }
    public void setTargetId(UUID targetId) { this.targetId = targetId; }
    public UUID getAuthorUserId() { return authorUserId; }
    public void setAuthorUserId(UUID authorUserId) { this.authorUserId = authorUserId; }
    public UUID getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(UUID parentCommentId) { this.parentCommentId = parentCommentId; }
    public UUID getRootCommentId() { return rootCommentId; }
    public void setRootCommentId(UUID rootCommentId) { this.rootCommentId = rootCommentId; }
    public int getDepth() { return depth; }
    public void setDepth(int depth) { this.depth = depth; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public CommentStatus getStatus() { return status; }
    public void setStatus(CommentStatus status) { this.status = status; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getRepliesCount() { return repliesCount; }
    public void setRepliesCount(int repliesCount) { this.repliesCount = repliesCount; }
}


