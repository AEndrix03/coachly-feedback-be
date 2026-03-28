package it.aredegalli.coachly.feedback.service;

import it.aredegalli.coachly.feedback.dto.CommentResponse;
import it.aredegalli.coachly.feedback.controller.request.CreateCommentRequest;
import it.aredegalli.coachly.feedback.controller.request.UpdateCommentRequest;
import it.aredegalli.coachly.feedback.model.TargetType;
import it.aredegalli.coachly.feedback.model.VoteType;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    CommentResponse create(CreateCommentRequest request);
    CommentResponse update(UUID id, UpdateCommentRequest request);
    void softDelete(UUID id);
    List<CommentResponse> list(TargetType targetType, UUID targetId, UUID parentId, String sort);
    CommentResponse vote(UUID commentId, VoteType voteType);
    CommentResponse removeVote(UUID commentId);
}

