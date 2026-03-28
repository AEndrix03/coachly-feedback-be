package com.coachly.feedback.poll.api;

import com.coachly.feedback.common.model.PollStatus;
import com.coachly.feedback.common.model.PollType;

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