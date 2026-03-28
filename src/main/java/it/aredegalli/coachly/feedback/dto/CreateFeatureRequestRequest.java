package it.aredegalli.coachly.feedback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFeatureRequestRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 4000) String description,
        @Size(max = 100) String category,
        @Size(max = 30) String platformTarget,
        @Size(max = 100) String moduleKey
) {
}


