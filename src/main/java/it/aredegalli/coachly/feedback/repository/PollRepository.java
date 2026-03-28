package it.aredegalli.coachly.feedback.repository;

import it.aredegalli.coachly.feedback.model.PollStatus;
import it.aredegalli.coachly.feedback.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PollRepository extends JpaRepository<Poll, UUID> {
    List<Poll> findByStatusIn(List<PollStatus> statuses);
}


