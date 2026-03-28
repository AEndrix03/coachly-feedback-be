package com.coachly.feedback.featurerequest.api;

import com.coachly.feedback.common.model.VoteType;
import jakarta.validation.constraints.NotNull;

public record FeatureVoteRequest(@NotNull VoteType voteType) {
}