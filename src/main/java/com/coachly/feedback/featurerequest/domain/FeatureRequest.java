package com.coachly.feedback.featurerequest.domain;

import com.coachly.feedback.common.model.AuditableEntity;
import com.coachly.feedback.common.model.FeatureRequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "feature_requests")
public class FeatureRequest extends AuditableEntity {

    @Column(nullable = false)
    private UUID authorUserId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    @Column(length = 100)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FeatureRequestStatus status = FeatureRequestStatus.NEW;

    @Column
    private UUID duplicateOfId;

    @Column(length = 30)
    private String platformTarget;

    @Column(length = 100)
    private String moduleKey;

    @Column(nullable = false)
    private int upvotesCount = 0;

    @Column(nullable = false)
    private int commentsCount = 0;

    public UUID getAuthorUserId() { return authorUserId; }
    public void setAuthorUserId(UUID authorUserId) { this.authorUserId = authorUserId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public FeatureRequestStatus getStatus() { return status; }
    public void setStatus(FeatureRequestStatus status) { this.status = status; }
    public UUID getDuplicateOfId() { return duplicateOfId; }
    public void setDuplicateOfId(UUID duplicateOfId) { this.duplicateOfId = duplicateOfId; }
    public String getPlatformTarget() { return platformTarget; }
    public void setPlatformTarget(String platformTarget) { this.platformTarget = platformTarget; }
    public String getModuleKey() { return moduleKey; }
    public void setModuleKey(String moduleKey) { this.moduleKey = moduleKey; }
    public int getUpvotesCount() { return upvotesCount; }
    public void setUpvotesCount(int upvotesCount) { this.upvotesCount = upvotesCount; }
    public int getCommentsCount() { return commentsCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }
}