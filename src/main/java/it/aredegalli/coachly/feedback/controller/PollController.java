package it.aredegalli.coachly.feedback.controller;

import it.aredegalli.coachly.feedback.dto.*;
import it.aredegalli.coachly.feedback.dto.ApiResponse;
import it.aredegalli.coachly.feedback.service.PollService;
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
public class PollController {

    private final PollService service;

    public PollController(PollService service) {
        this.service = service;
    }

    @PostMapping("/admin/polls")
    public ApiResponse<PollResponseDto> create(@Valid @RequestBody CreatePollRequest request) {
        return ApiResponse.of(service.create(request));
    }

    @GetMapping("/polls")
    public ApiResponse<List<PollResponseDto>> list() {
        return ApiResponse.of(service.listVisible());
    }

    @GetMapping("/polls/{id}")
    public ApiResponse<PollResponseDto> get(@PathVariable UUID id) {
        return ApiResponse.of(service.get(id));
    }

    @PostMapping("/polls/{id}/responses")
    public void answer(@PathVariable UUID id, @Valid @RequestBody PollResponseRequest request) {
        service.answer(id, request);
    }

    @GetMapping("/polls/{id}/results")
    public ApiResponse<PollResultResponse> results(@PathVariable UUID id) {
        return ApiResponse.of(service.results(id));
    }

    @PatchMapping("/admin/polls/{id}/publish")
    public ApiResponse<PollResponseDto> publish(@PathVariable UUID id) {
        return ApiResponse.of(service.publish(id));
    }

    @PatchMapping("/admin/polls/{id}/close")
    public ApiResponse<PollResponseDto> close(@PathVariable UUID id) {
        return ApiResponse.of(service.close(id));
    }
}






