package com.bialem.backend.repository;

import com.bialem.backend.domain.Comment;
import com.bialem.backend.domain.enumeration.CommentTargetType;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Comment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTargetTypeAndTargetIdOrderByCreatedAtDesc(CommentTargetType targetType, String targetId);

    long countByAuthor_Id(Long authorId);
}
