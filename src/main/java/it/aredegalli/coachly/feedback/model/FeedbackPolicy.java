package it.aredegalli.coachly.feedback.model;

import it.aredegalli.coachly.feedback.exception.BadRequestException;
import it.aredegalli.coachly.feedback.model.FeedbackType;
import org.springframework.stereotype.Component;

@Component
public class FeedbackPolicy {

    public void validateForType(FeedbackType type, Integer ratingValue, Object severity) {
        if (type == FeedbackType.REVIEW && ratingValue == null) {
            throw new BadRequestException("RATING_REQUIRED", "Rating is required for REVIEW feedback");
        }
        if (type != FeedbackType.REVIEW && ratingValue != null) {
            throw new BadRequestException("RATING_NOT_ALLOWED", "Rating is allowed only for REVIEW feedback");
        }
        if (type == FeedbackType.BUG && severity == null) {
            throw new BadRequestException("SEVERITY_REQUIRED", "Severity is required for BUG feedback");
        }
    }
}


