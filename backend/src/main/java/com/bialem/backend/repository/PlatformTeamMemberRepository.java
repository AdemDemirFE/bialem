package com.bialem.backend.repository;

import com.bialem.backend.domain.PlatformTeamMember;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PlatformTeamMember entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlatformTeamMemberRepository extends JpaRepository<PlatformTeamMember, Long> {}
