package it.aredegalli.coachly.feedback.comment.domain;

import it.aredegalli.coachly.feedback.config.AppProperties;
import it.aredegalli.coachly.feedback.exception.BadRequestException;
import it.aredegalli.coachly.feedback.model.CommentPolicy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommentPolicyTest {

    private final CommentPolicy policy = new CommentPolicy(
            new AppProperties(
                    new AppProperties.SecurityHeaders("X-User-Id", "X-User-Role", "X-Correlation-Id"),
                    new AppProperties.Comments(4, List.of("UP")),
                    new AppProperties.Features(List.of("NEW"), true),
                    new AppProperties.Polls(true),
                    new AppProperties.Ranking(0.5)
            )
    );

    @Test
    void shouldValidateDepth() {
        assertDoesNotThrow(() -> policy.validateDepth(4));
        assertThrows(BadRequestException.class, () -> policy.validateDepth(5));
    }
}
