package it.aredegalli.coachly.feedback.dto;

import it.aredegalli.coachly.feedback.model.PollStatus;
import it.aredegalli.coachly.feedback.model.PollType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PollResponseDto(
        UUID id,
        String title,
        String description,
        PollType type,
        PollStatus status,
        boolean multipleChoice,
        boolean anonymousResults,
        Instant opensAt,
        Instant closesAt,
        List<PollOptionDto> options
) {
    public record PollOptionDto(UUID id, String label, int position) {}
}


