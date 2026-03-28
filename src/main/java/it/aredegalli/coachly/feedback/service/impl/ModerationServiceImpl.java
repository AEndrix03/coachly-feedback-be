package it.aredegalli.coachly.feedback.service.impl;

import it.aredegalli.coachly.feedback.repository.CommentRepository;
import it.aredegalli.coachly.feedback.exception.NotFoundException;
import it.aredegalli.coachly.feedback.model.CommentStatus;
import it.aredegalli.coachly.feedback.model.ContentVisibility;
import it.aredegalli.coachly.feedback.model.ModerationReportStatus;
import it.aredegalli.coachly.feedback.model.TargetType;
import it.aredegalli.coachly.feedback.security.AuthorizationService;
import it.aredegalli.coachly.feedback.security.RequestUserContext;
import it.aredegalli.coachly.feedback.security.RequestUserContextResolver;
import it.aredegalli.coachly.feedback.time.TimeProvider;
import it.aredegalli.coachly.feedback.repository.FeedbackEntryRepository;
import it.aredegalli.coachly.feedback.repository.FeatureRequestRepository;
import it.aredegalli.coachly.feedback.controller.request.CreateModerationReportRequest;
import it.aredegalli.coachly.feedback.model.ModerationReport;
import it.aredegalli.coachly.feedback.repository.ModerationReportRepository;
import it.aredegalli.coachly.feedback.service.ModerationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ModerationServiceImpl implements ModerationService {

    private final ModerationReportRepository reportRepository;
    private final RequestUserContextResolver contextResolver;
    private final AuthorizationService authorizationService;
    private final TimeProvider timeProvider;
    private final FeedbackEntryRepository feedbackRepository;
    private final FeatureRequestRepository featureRequestRepository;
    private final CommentRepository commentRepository;

    public ModerationServiceImpl(ModerationReportRepository reportRepository,
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



