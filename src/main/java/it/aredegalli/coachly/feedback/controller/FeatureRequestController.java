package it.aredegalli.coachly.feedback.controller;

import it.aredegalli.coachly.feedback.controller.request.ChangeFeatureStatusRequest;
import it.aredegalli.coachly.feedback.controller.request.CreateFeatureRequestRequest;
import it.aredegalli.coachly.feedback.controller.request.FeatureVoteRequest;
import it.aredegalli.coachly.feedback.controller.request.UpdateFeatureRequestRequest;
import it.aredegalli.coachly.feedback.dto.ApiResponse;
import it.aredegalli.coachly.feedback.dto.FeatureRequestResponse;
import it.aredegalli.coachly.feedback.dto.PagedResponse;
import it.aredegalli.coachly.feedback.model.StatusHistory;
import it.aredegalli.coachly.feedback.service.FeatureRequestService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class FeatureRequestController {

    private final FeatureRequestService service;

    public FeatureRequestController(FeatureRequestService service) {
        this.service = service;
    }

    @PostMapping("/feature-requests")
    public ApiResponse<FeatureRequestResponse> create(@Valid @RequestBody CreateFeatureRequestRequest request) {
        return ApiResponse.of(service.create(request));
    }

    @PutMapping("/feature-requests/{id}")
    public ApiResponse<FeatureRequestResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateFeatureRequestRequest request) {
        return ApiResponse.of(service.update(id, request));
    }

    @GetMapping("/feature-requests/{id}")
    public ApiResponse<FeatureRequestResponse> getById(@PathVariable UUID id) {
        return ApiResponse.of(service.getById(id));
    }

    @GetMapping("/feature-requests")
    public ApiResponse<PagedResponse<FeatureRequestResponse>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(required = false) String moduleKey,
            @RequestParam(required = false) String platformTarget,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.of(service.list(status, category, search, sort, moduleKey, platformTarget, page, size));
    }

    @PostMapping("/feature-requests/{id}/vote")
    public ApiResponse<FeatureRequestResponse> vote(@PathVariable UUID id, @Valid @RequestBody FeatureVoteRequest request) {
        return ApiResponse.of(service.vote(id, request.voteType()));
    }

    @DeleteMapping("/feature-requests/{id}/vote")
    public ApiResponse<FeatureRequestResponse> removeVote(@PathVariable UUID id) {
        return ApiResponse.of(service.removeVote(id));
    }

    @PatchMapping("/admin/feature-requests/{id}/status")
    public ApiResponse<FeatureRequestResponse> changeStatus(@PathVariable UUID id,
                                                            @Valid @RequestBody ChangeFeatureStatusRequest request) {
        return ApiResponse.of(service.changeStatus(id, request));
    }

    @PatchMapping("/admin/feature-requests/{id}/duplicate-of/{masterId}")
    public ApiResponse<FeatureRequestResponse> markDuplicate(@PathVariable UUID id, @PathVariable UUID masterId) {
        return ApiResponse.of(service.markDuplicate(id, masterId));
    }

    @GetMapping("/feature-requests/{id}/history")
    public ApiResponse<List<StatusHistory>> history(@PathVariable UUID id) {
        return ApiResponse.of(service.history(id));
    }
}

