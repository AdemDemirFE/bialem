package com.bialem.backend.repository;

import com.bialem.backend.domain.CommunityMember;
import com.bialem.backend.domain.enumeration.CommunityMemberRole;
import com.bialem.backend.domain.enumeration.CommunityMemberStatus;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CommunityMember entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {
    @EntityGraph(attributePaths = { "user", "user.user" })
    @Query("""
        select cm from CommunityMember cm
        where cm.community.id = :communityId and cm.status = :status
          and (:search = '' or lower(cm.user.displayName) like lower(concat('%', :search, '%'))
            or lower(cm.user.username) like lower(concat('%', :search, '%'))
            or lower(cm.user.user.login) like lower(concat('%', :search, '%')))
        """)
    Page<CommunityMember> findManagementMembers(
        @Param("communityId") Long communityId,
        @Param("status") CommunityMemberStatus status,
        @Param("search") String search,
        Pageable pageable
    );

    long countByCommunityIdAndStatus(Long communityId, CommunityMemberStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = { "community", "user", "user.user" })
    @Query("select cm from CommunityMember cm where cm.id = :id and cm.community.id = :communityId")
    Optional<CommunityMember> findForManagementUpdate(@Param("communityId") Long communityId, @Param("id") Long id);

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

    @Query("select distinct cm.user.user from CommunityMember cm where cm.community.id = :communityId and cm.status = :status and cm.user.user.activated = true")
    List<com.bialem.backend.domain.User> findRecipientUsers(
        @Param("communityId") Long communityId,
        @Param("status") CommunityMemberStatus status
    );

    @EntityGraph(attributePaths = { "user", "community" })
    @Query("select cm from CommunityMember cm where (:userId is null or cm.user.id = :userId) and (:communityId is null or cm.community.id = :communityId) and (:status is null or cm.status = :status)")
    List<CommunityMember> findAllByUserIdAndCommunityIdAndStatus(
        @Param("userId") Long userId,
        @Param("communityId") Long communityId,
        @Param("status") CommunityMemberStatus status
    );
}
