package com.bialem.backend.repository;

import com.bialem.backend.domain.CommunityMember;
import com.bialem.backend.domain.enumeration.CommunityMemberRole;
import com.bialem.backend.domain.enumeration.CommunityMemberStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CommunityMember entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {
    @Query("select cm from CommunityMember cm where cm.community.id = :communityId and cm.user.user.login = :login and cm.status = :status")
    Optional<CommunityMember> findMembership(
        @Param("communityId") Long communityId,
        @Param("login") String login,
        @Param("status") CommunityMemberStatus status
    );
    @Query(
        "SELECT cm FROM CommunityMember cm JOIN FETCH cm.user WHERE cm.community.id = :communityId AND cm.role IN :roles AND cm.status = :status"
    )
    List<CommunityMember> findManagersByCommunityId(
        @Param("communityId") Long communityId,
        @Param("roles") List<CommunityMemberRole> roles,
        @Param("status") CommunityMemberStatus status
    );
}
