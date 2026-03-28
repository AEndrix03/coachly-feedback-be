package com.coachly.feedback.poll.infrastructure;

import com.coachly.feedback.common.model.PollStatus;
import com.coachly.feedback.poll.domain.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PollRepository extends JpaRepository<Poll, UUID> {
    List<Poll> findByStatusIn(List<PollStatus> statuses);
}