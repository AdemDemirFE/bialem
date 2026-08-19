package com.bialem.backend.repository;

import com.bialem.backend.domain.PostMedia;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PostMedia entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PostMediaRepository extends JpaRepository<PostMedia, Long> {}
