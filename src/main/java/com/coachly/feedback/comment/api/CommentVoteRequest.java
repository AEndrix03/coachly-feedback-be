package com.coachly.feedback.comment.api;

import com.coachly.feedback.common.model.VoteType;
import jakarta.validation.constraints.NotNull;

public record CommentVoteRequest(@NotNull VoteType voteType) {
}