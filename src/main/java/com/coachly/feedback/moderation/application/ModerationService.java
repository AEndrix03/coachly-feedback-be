package com.coachly.feedback.moderation.application;

import com.coachly.feedback.comment.infrastructure.CommentRepository;
import com.coachly.feedback.common.exception.NotFoundException;
import com.coachly.feedback.common.model.CommentStatus;
import com.coachly.feedback.common.model.ContentVisibility;
import com.coachly.feedback.common.model.ModerationReportStatus;
import com.coachly.feedback.common.model.TargetType;
import com.coachly.feedback.common.security.AuthorizationService;
import com.coachly.feedback.common.security.RequestUserContext;
import com.coachly.feedback.common.security.RequestUserContextResolver;
import com.coachly.feedback.common.time.TimeProvider;
import com.coachly.feedback.feedback.infrastructure.FeedbackEntryRepository;
import com.coachly.feedback.featurerequest.infrastructure.FeatureRequestRepository;
import com.coachly.feedback.moderation.api.CreateModerationReportRequest;
import com.coachly.feedback.moderation.domain.ModerationReport;
import com.coachly.feedback.moderation.infrastructure.ModerationReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ModerationService {

    private final ModerationReportRepository reportRepository;
    private final RequestUserContextResolver contextResolver;
    private final AuthorizationService authorizationService;
    private final TimeProvider timeProvider;
    private final FeedbackEntryRepository feedbackRepository;
    private final FeatureRequestRepository featureRequestRepository;
    private final CommentRepository commentRepository;

    public ModerationService(ModerationReportRepository reportRepository,
                             RequestUserContextResolver contextResolver,
                             AuthorizationService authorizationService,
                             TimeProvider timeProvider,
                             FeedbackEntryRepository feedbackRepository,
                             FeatureRequestRepository featureRequestRepository,
                             CommentRepository commentRepository) {
        this.reportRepository = reportRepository;
        this.contextResolver = contextResolver;
        this.authorizationService = authorizationService;
        this.timeProvider = timeProvider;
        this.feedbackRepository = feedbackRepository;
        this.featureRequestRepository = featureRequestRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public ModerationReport create(CreateModerationReportRequest request) {
        RequestUserContext context = contextResolver.getRequiredContext();
        ModerationReport report = new ModerationReport();
        report.setReporterUserId(context.userId());
        report.setTargetType(request.targetType());
        report.setTargetId(request.targetId());
        report.setReason(request.reason());
        report.setDetails(request.details());
        return reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public List<ModerationReport> list() {
        authorizationService.requireModeratorOrAdmin(contextResolver.getRequiredContext());
        return reportRepository.findAll();
    }

    @Transactional
    public ModerationReport updateStatus(UUID reportId, ModerationReportStatus status) {
        RequestUserContext context = contextResolver.getRequiredContext();
        authorizationService.requireModeratorOrAdmin(context);
        ModerationReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new NotFoundException("REPORT_NOT_FOUND", "Moderation report not found"));
        report.setStatus(status);
        if (status != ModerationReportStatus.OPEN) {
            report.setResolvedAt(timeProvider.now());
            report.setResolvedByUserId(context.userId());
        }
        return reportRepository.save(report);
    }

    @Transactional
    public void hide(TargetType targetType, UUID targetId) {
        authorizationService.requireModeratorOrAdmin(contextResolver.getRequiredContext());
        switch (targetType) {
            case FEEDBACK_ENTRY -> {
                var feedback = feedbackRepository.findById(targetId)
                        .orElseThrow(() -> new NotFoundException("FEEDBACK_NOT_FOUND", "Feedback not found"));
                feedback.setVisibility(ContentVisibility.HIDDEN);
                feedbackRepository.save(feedback);
            }
            case FEATURE_REQUEST -> {
                var feature = featureRequestRepository.findById(targetId)
                        .orElseThrow(() -> new NotFoundException("FEATURE_REQUEST_NOT_FOUND", "Feature request not found"));
                feature.setDeletedAt(timeProvider.now());
                featureRequestRepository.save(feature);
            }
            case POLL -> throw new NotFoundException("POLL_HIDE_NOT_SUPPORTED", "Hide is not supported for polls");
            default -> {
                var comment = commentRepository.findById(targetId)
                        .orElseThrow(() -> new NotFoundException("COMMENT_NOT_FOUND", "Comment not found"));
                comment.setStatus(CommentStatus.HIDDEN);
                commentRepository.save(comment);
            }
        }
    }

    @Transactional
    public void restore(TargetType targetType, UUID targetId) {
        authorizationService.requireModeratorOrAdmin(contextResolver.getRequiredContext());
        switch (targetType) {
            case FEEDBACK_ENTRY -> {
                var feedback = feedbackRepository.findById(targetId)
                        .orElseThrow(() -> new NotFoundException("FEEDBACK_NOT_FOUND", "Feedback not found"));
                feedback.setVisibility(ContentVisibility.PUBLIC);
                feedbackRepository.save(feedback);
            }
            case FEATURE_REQUEST -> {
                var feature = featureRequestRepository.findById(targetId)
                        .orElseThrow(() -> new NotFoundException("FEATURE_REQUEST_NOT_FOUND", "Feature request not found"));
                feature.setDeletedAt(null);
                featureRequestRepository.save(feature);
            }
            case POLL -> throw new NotFoundException("POLL_RESTORE_NOT_SUPPORTED", "Restore is not supported for polls");
            default -> {
                var comment = commentRepository.findById(targetId)
                        .orElseThrow(() -> new NotFoundException("COMMENT_NOT_FOUND", "Comment not found"));
                comment.setStatus(CommentStatus.ACTIVE);
                commentRepository.save(comment);
            }
        }
    }
}