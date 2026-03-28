package it.aredegalli.coachly.feedback.repository;

import it.aredegalli.coachly.feedback.model.Comment;
import it.aredegalli.coachly.feedback.model.TargetType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByTargetTypeAndTargetIdAndParentCommentIdAndDeletedAtIsNull(TargetType targetType, UUID targetId, UUID parentCommentId, Sort sort);
    List<Comment> findByTargetTypeAndTargetIdAndParentCommentIdIsNullAndDeletedAtIsNull(TargetType targetType, UUID targetId, Sort sort);
    Optional<Comment> findByIdAndDeletedAtIsNull(UUID id);
    long countByTargetTypeAndTargetIdAndDeletedAtIsNull(TargetType targetType, UUID targetId);
}


