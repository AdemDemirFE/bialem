package com.bialem.backend.repository;

import com.bialem.backend.domain.CommunityMember;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CommunityMember entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {}
