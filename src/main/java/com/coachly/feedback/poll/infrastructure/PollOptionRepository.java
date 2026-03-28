package com.coachly.feedback.poll.infrastructure;

import com.coachly.feedback.poll.domain.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PollOptionRepository extends JpaRepository<PollOption, UUID> {
    List<PollOption> findByPollIdOrderByPositionAsc(UUID pollId);
    boolean existsByIdAndPollId(UUID id, UUID pollId);
}