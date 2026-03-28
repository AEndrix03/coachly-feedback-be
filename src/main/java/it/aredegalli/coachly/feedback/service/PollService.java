package it.aredegalli.coachly.feedback.service;

import it.aredegalli.coachly.feedback.dto.PollResponseDto;
import it.aredegalli.coachly.feedback.dto.PollResultResponse;
import it.aredegalli.coachly.feedback.controller.request.CreatePollRequest;
import it.aredegalli.coachly.feedback.controller.request.PollResponseRequest;

import java.util.List;
import java.util.UUID;

public interface PollService {
    PollResponseDto create(CreatePollRequest request);
    List<PollResponseDto> listVisible();
    PollResponseDto get(UUID pollId);
    void answer(UUID pollId, PollResponseRequest request);
    PollResultResponse results(UUID pollId);
    PollResponseDto publish(UUID pollId);
    PollResponseDto close(UUID pollId);
}

