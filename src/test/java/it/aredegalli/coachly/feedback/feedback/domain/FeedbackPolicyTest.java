package it.aredegalli.coachly.feedback.feedback.domain;

import it.aredegalli.coachly.feedback.exception.BadRequestException;
import it.aredegalli.coachly.feedback.model.FeedbackPolicy;
import it.aredegalli.coachly.feedback.model.FeedbackType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FeedbackPolicyTest {

    private final FeedbackPolicy policy = new FeedbackPolicy();

    @Test
    void shouldRequireRatingForReview() {
        assertThrows(BadRequestException.class, () -> policy.validateForType(FeedbackType.REVIEW, null, null));
    }

    @Test
    void shouldAllowBugWithSeverity() {
        assertDoesNotThrow(() -> policy.validateForType(FeedbackType.BUG, null, "HIGH"));
    }
}
