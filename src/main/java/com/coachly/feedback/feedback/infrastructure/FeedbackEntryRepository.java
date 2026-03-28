package com.coachly.feedback.feedback.infrastructure;

import com.coachly.feedback.common.model.FeedbackType;
import com.coachly.feedback.common.model.TargetType;
import com.coachly.feedback.feedback.domain.FeedbackEntry;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeedbackEntryRepository extends JpaRepository<FeedbackEntry, UUID>, JpaSpecificationExecutor<FeedbackEntry> {

    Optional<FeedbackEntry> findByAuthorUserIdAndTypeAndTargetTypeAndTargetId(UUID authorUserId, FeedbackType type, TargetType targetType, UUID targetId);

    @Query("select avg(f.ratingValue) from FeedbackEntry f where f.type = com.coachly.feedback.common.model.FeedbackType.REVIEW and f.targetType = :targetType and f.targetId = :targetId and f.deletedAt is null")
    Double averageRating(@Param("targetType") TargetType targetType, @Param("targetId") UUID targetId);

    @Query("select count(f) from FeedbackEntry f where f.type = com.coachly.feedback.common.model.FeedbackType.REVIEW and f.targetType = :targetType and f.targetId = :targetId and f.deletedAt is null")
    long reviewsCount(@Param("targetType") TargetType targetType, @Param("targetId") UUID targetId);

    @Query("select f.ratingValue, count(f) from FeedbackEntry f where f.type = com.coachly.feedback.common.model.FeedbackType.REVIEW and f.targetType = :targetType and f.targetId = :targetId and f.deletedAt is null group by f.ratingValue")
    List<Object[]> ratingDistribution(@Param("targetType") TargetType targetType, @Param("targetId") UUID targetId);

    static Specification<FeedbackEntry> hasFilters(String type, String targetType, UUID targetId, String category, UUID authorUserId, String status) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            predicates = cb.and(predicates, cb.isNull(root.get("deletedAt")));
            if (type != null) predicates = cb.and(predicates, cb.equal(root.get("type"), type));
            if (targetType != null) predicates = cb.and(predicates, cb.equal(root.get("targetType"), targetType));
            if (targetId != null) predicates = cb.and(predicates, cb.equal(root.get("targetId"), targetId));
            if (category != null) predicates = cb.and(predicates, cb.equal(root.get("category"), category));
            if (authorUserId != null) predicates = cb.and(predicates, cb.equal(root.get("authorUserId"), authorUserId));
            if (status != null) predicates = cb.and(predicates, cb.equal(root.get("status"), status));
            return predicates;
        };
    }
}