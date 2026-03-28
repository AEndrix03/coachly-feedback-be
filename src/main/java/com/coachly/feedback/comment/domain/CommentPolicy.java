package com.coachly.feedback.comment.domain;

import com.coachly.feedback.common.config.AppProperties;
import com.coachly.feedback.common.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class CommentPolicy {

    private final AppProperties appProperties;

    public CommentPolicy(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public void validateDepth(int depth) {
        if (depth > appProperties.comments().maxDepth()) {
            throw new BadRequestException("COMMENT_MAX_DEPTH_EXCEEDED", "Max comment depth exceeded");
        }
    }
}