package it.aredegalli.coachly.feedback.dto;

import it.aredegalli.coachly.feedback.model.VoteType;
import jakarta.validation.constraints.NotNull;

public record CommentVoteRequest(@NotNull VoteType voteType) {
}


