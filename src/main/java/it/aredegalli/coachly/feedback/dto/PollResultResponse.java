package it.aredegalli.coachly.feedback.dto;

import java.util.List;
import java.util.UUID;

public record PollResultResponse(UUID pollId, long participants, List<OptionResult> options) {
    public record OptionResult(UUID optionId, String label, long votes, double percentage) {}
}


