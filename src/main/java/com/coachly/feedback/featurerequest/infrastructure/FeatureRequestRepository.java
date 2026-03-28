package com.coachly.feedback.featurerequest.infrastructure;

import com.coachly.feedback.featurerequest.domain.FeatureRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface FeatureRequestRepository extends JpaRepository<FeatureRequest, UUID>, JpaSpecificationExecutor<FeatureRequest> {

    static Specification<FeatureRequest> hasFilters(String status, String category, String search, String moduleKey, String platformTarget) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            predicates = cb.and(predicates, cb.isNull(root.get("deletedAt")));
            if (status != null) predicates = cb.and(predicates, cb.equal(root.get("status"), status));
            if (category != null) predicates = cb.and(predicates, cb.equal(root.get("category"), category));
            if (moduleKey != null) predicates = cb.and(predicates, cb.equal(root.get("moduleKey"), moduleKey));
            if (platformTarget != null) predicates = cb.and(predicates, cb.equal(root.get("platformTarget"), platformTarget));
            if (search != null && !search.isBlank()) {
                String q = "%" + search.toLowerCase() + "%";
                predicates = cb.and(predicates,
                        cb.or(
                                cb.like(cb.lower(root.get("title")), q),
                                cb.like(cb.lower(root.get("description")), q)
                        )
                );
            }
            return predicates;
        };
    }
}