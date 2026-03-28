package com.coachly.feedback.featurerequest.api;

import com.coachly.feedback.common.model.FeatureRequestStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangeFeatureStatusRequest(
        @NotNull FeatureRequestStatus status,
        @Size(max = 1000) String publicNote,
        @Size(max = 1000) String internalNote
) {
}