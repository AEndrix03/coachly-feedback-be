package it.aredegalli.coachly.feedback.controller;

import it.aredegalli.coachly.feedback.dto.*;
import it.aredegalli.coachly.feedback.controller.request.CreateModerationReportRequest;
import it.aredegalli.coachly.feedback.controller.request.UpdateModerationReportStatusRequest;
import it.aredegalli.coachly.feedback.dto.ApiResponse;
import it.aredegalli.coachly.feedback.model.TargetType;
import it.aredegalli.coachly.feedback.service.ModerationService;
import it.aredegalli.coachly.feedback.model.ModerationReport;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ModerationController {

    private final ModerationService service;

    public ModerationController(ModerationService service) {
        this.service = service;
    }

    @PostMapping("/reports")
    public ApiResponse<ModerationReport> createReport(@Valid @RequestBody CreateModerationReportRequest request) {
        return ApiResponse.of(service.create(request));
    }

    @GetMapping("/admin/reports")
    public ApiResponse<List<ModerationReport>> listReports() {
        return ApiResponse.of(service.list());
    }

    @PatchMapping("/admin/reports/{id}/status")
    public ApiResponse<ModerationReport> updateReportStatus(@PathVariable UUID id,
                                                             @Valid @RequestBody UpdateModerationReportStatusRequest request) {
        return ApiResponse.of(service.updateStatus(id, request.status()));
    }

    @PatchMapping("/admin/content/{targetType}/{targetId}/hide")
    public void hide(@PathVariable TargetType targetType, @PathVariable UUID targetId) {
        service.hide(targetType, targetId);
    }

    @PatchMapping("/admin/content/{targetType}/{targetId}/restore")
    public void restore(@PathVariable TargetType targetType, @PathVariable UUID targetId) {
        service.restore(targetType, targetId);
    }
}






