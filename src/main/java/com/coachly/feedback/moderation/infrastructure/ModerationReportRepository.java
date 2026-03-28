package com.coachly.feedback.moderation.infrastructure;

import com.coachly.feedback.moderation.domain.ModerationReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ModerationReportRepository extends JpaRepository<ModerationReport, UUID> {
}