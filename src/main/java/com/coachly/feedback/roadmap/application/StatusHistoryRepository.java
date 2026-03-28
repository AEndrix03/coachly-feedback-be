package com.coachly.feedback.roadmap.application;

import com.coachly.feedback.common.model.StatusHistoryTargetType;
import com.coachly.feedback.roadmap.domain.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, UUID> {
    List<StatusHistory> findByTargetTypeAndTargetIdOrderByChangedAtDesc(StatusHistoryTargetType targetType, UUID targetId);
}