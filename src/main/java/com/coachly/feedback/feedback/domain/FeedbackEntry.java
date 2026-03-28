package com.coachly.feedback.feedback.domain;

import com.coachly.feedback.common.model.AuditableEntity;
import com.coachly.feedback.common.model.ContentVisibility;
import com.coachly.feedback.common.model.FeedbackStatus;
import com.coachly.feedback.common.model.FeedbackType;
import com.coachly.feedback.common.model.IssueSeverity;
import com.coachly.feedback.common.model.TargetType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "feedback_entries")
public class FeedbackEntry extends AuditableEntity {

    @Column(nullable = false)
    private UUID authorUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FeedbackType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FeedbackStatus status = FeedbackStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ContentVisibility visibility = ContentVisibility.PUBLIC;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 4000)
    private String body;

    @Column
    private Integer ratingValue;

    @Column(length = 100)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TargetType targetType;

    @Column(nullable = false)
    private UUID targetId;

    @Column(length = 100)
    private String featureKey;

    @Column(length = 100)
    private String screenKey;

    @Column(length = 100)
    private String flowKey;

    @Column(length = 30)
    private String platform;

    @Column(length = 30)
    private String appVersion;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private IssueSeverity severity;

    @Column
    private Boolean reproducible;

    public UUID getAuthorUserId() { return authorUserId; }
    public void setAuthorUserId(UUID authorUserId) { this.authorUserId = authorUserId; }
    public FeedbackType getType() { return type; }
    public void setType(FeedbackType type) { this.type = type; }
    public FeedbackStatus getStatus() { return status; }
    public void setStatus(FeedbackStatus status) { this.status = status; }
    public ContentVisibility getVisibility() { return visibility; }
    public void setVisibility(ContentVisibility visibility) { this.visibility = visibility; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Integer getRatingValue() { return ratingValue; }
    public void setRatingValue(Integer ratingValue) { this.ratingValue = ratingValue; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public TargetType getTargetType() { return targetType; }
    public void setTargetType(TargetType targetType) { this.targetType = targetType; }
    public UUID getTargetId() { return targetId; }
    public void setTargetId(UUID targetId) { this.targetId = targetId; }
    public String getFeatureKey() { return featureKey; }
    public void setFeatureKey(String featureKey) { this.featureKey = featureKey; }
    public String getScreenKey() { return screenKey; }
    public void setScreenKey(String screenKey) { this.screenKey = screenKey; }
    public String getFlowKey() { return flowKey; }
    public void setFlowKey(String flowKey) { this.flowKey = flowKey; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }
    public IssueSeverity getSeverity() { return severity; }
    public void setSeverity(IssueSeverity severity) { this.severity = severity; }
    public Boolean getReproducible() { return reproducible; }
    public void setReproducible(Boolean reproducible) { this.reproducible = reproducible; }
}