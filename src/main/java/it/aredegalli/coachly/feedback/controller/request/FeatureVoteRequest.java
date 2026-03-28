package it.aredegalli.coachly.feedback.controller.request;

import it.aredegalli.coachly.feedback.model.VoteType;
import jakarta.validation.constraints.NotNull;

public record FeatureVoteRequest(@NotNull VoteType voteType) {
}



