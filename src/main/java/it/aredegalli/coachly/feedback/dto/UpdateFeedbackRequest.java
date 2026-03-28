package it.aredegalli.coachly.feedback.dto;

import it.aredegalli.coachly.feedback.model.IssueSeverity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateFeedbackRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 4000) String body,
        @Min(1) @Max(5) Integer ratingValue,
        @Size(max = 100) String category,
        @Size(max = 100) String featureKey,
        @Size(max = 100) String screenKey,
        @Size(max = 100) String flowKey,
        @Size(max = 30) String platform,
        @Size(max = 30) String appVersion,
        IssueSeverity severity,
        Boolean reproducible
) {
}


