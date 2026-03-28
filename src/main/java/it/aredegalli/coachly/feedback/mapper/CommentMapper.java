package it.aredegalli.coachly.feedback.mapper;

import it.aredegalli.coachly.feedback.dto.CommentResponse;
import it.aredegalli.coachly.feedback.model.Comment;

public final class CommentMapper {
    private CommentMapper() {}

    public static CommentResponse toResponse(Comment c) {
        return new CommentResponse(
                c.getId(), c.getTargetType(), c.getTargetId(), c.getAuthorUserId(),
                c.getParentCommentId(), c.getRootCommentId(), c.getDepth(), c.getBody(),
                c.getStatus(), c.getScore(), c.getRepliesCount(), c.getCreatedAt(), c.getUpdatedAt());
    }
}


