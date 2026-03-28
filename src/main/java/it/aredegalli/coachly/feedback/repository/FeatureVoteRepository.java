package it.aredegalli.coachly.feedback.repository;

import it.aredegalli.coachly.feedback.model.FeatureVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FeatureVoteRepository extends JpaRepository<FeatureVote, UUID> {
    Optional<FeatureVote> findByFeatureRequestIdAndUserId(UUID featureRequestId, UUID userId);
    long countByFeatureRequestId(UUID featureRequestId);
}


