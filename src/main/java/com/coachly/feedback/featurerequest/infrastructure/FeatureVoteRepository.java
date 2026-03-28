package com.coachly.feedback.featurerequest.infrastructure;

import com.coachly.feedback.featurerequest.domain.FeatureVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FeatureVoteRepository extends JpaRepository<FeatureVote, UUID> {
    Optional<FeatureVote> findByFeatureRequestIdAndUserId(UUID featureRequestId, UUID userId);
    long countByFeatureRequestId(UUID featureRequestId);
}