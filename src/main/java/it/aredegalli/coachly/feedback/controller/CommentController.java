package it.aredegalli.coachly.feedback.controller;

import it.aredegalli.coachly.feedback.dto.*;
import it.aredegalli.coachly.feedback.service.CommentService;
import it.aredegalli.coachly.feedback.dto.ApiResponse;
import it.aredegalli.coachly.feedback.model.TargetType;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @PostMapping
    public ApiResponse<CommentResponse> create(@Valid @RequestBody CreateCommentRequest request) {
        return ApiResponse.of(service.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<CommentResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateCommentRequest request) {
        return ApiResponse.of(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.softDelete(id);
    }

    @GetMapping
    public ApiResponse<List<CommentResponse>> list(
            @RequestParam TargetType targetType,
            @RequestParam UUID targetId,
            @RequestParam(required = false) UUID parentId,
            @RequestParam(defaultValue = "top") String sort
    ) {
        return ApiResponse.of(service.list(targetType, targetId, parentId, sort));
    }

    @PostMapping("/{id}/vote")
    public ApiResponse<CommentResponse> vote(@PathVariable UUID id, @Valid @RequestBody CommentVoteRequest request) {
        return ApiResponse.of(service.vote(id, request.voteType()));
    }

    @DeleteMapping("/{id}/vote")
    public ApiResponse<CommentResponse> removeVote(@PathVariable UUID id) {
        return ApiResponse.of(service.removeVote(id));
    }
}






