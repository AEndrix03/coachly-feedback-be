package it.aredegalli.coachly.feedback.repository;

import it.aredegalli.coachly.feedback.model.PollResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PollResponseRepository extends JpaRepository<PollResponse, UUID> {
    Optional<PollResponse> findByPollIdAndUserId(UUID pollId, UUID userId);
    long countByPollId(UUID pollId);
    List<PollResponse> findByPollId(UUID pollId);
}


