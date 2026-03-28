package it.aredegalli.coachly.feedback.repository;

import it.aredegalli.coachly.feedback.model.StatusHistoryTargetType;
import it.aredegalli.coachly.feedback.model.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, UUID> {
    List<StatusHistory> findByTargetTypeAndTargetIdOrderByChangedAtDesc(StatusHistoryTargetType targetType, UUID targetId);
}


