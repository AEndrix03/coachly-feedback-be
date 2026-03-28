package com.coachly.feedback.comment.mapper;

import com.coachly.feedback.comment.api.CommentResponse;
import com.coachly.feedback.comment.domain.Comment;

public final class CommentMapper {
    private CommentMapper() {}

    public static CommentResponse toResponse(Comment c) {
        return new CommentResponse(
                c.getId(), c.getTargetType(), c.getTargetId(), c.getAuthorUserId(),
                c.getParentCommentId(), c.getRootCommentId(), c.getDepth(), c.getBody(),
                c.getStatus(), c.getScore(), c.getRepliesCount(), c.getCreatedAt(), c.getUpdatedAt());
    }
}