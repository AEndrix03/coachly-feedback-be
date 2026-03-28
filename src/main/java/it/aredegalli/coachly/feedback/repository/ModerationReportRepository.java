package it.aredegalli.coachly.feedback.repository;

import it.aredegalli.coachly.feedback.model.ModerationReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ModerationReportRepository extends JpaRepository<ModerationReport, UUID> {
}


