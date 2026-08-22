package com.bialem.backend.repository;

import com.bialem.backend.domain.Profile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Profile entity.
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long>, JpaSpecificationExecutor<Profile> {
    default Optional<Profile> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Profile> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Profile> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select profile from Profile profile left join fetch profile.user",
        countQuery = "select count(profile) from Profile profile"
    )
    Page<Profile> findAllWithToOneRelationships(Pageable pageable);

    @Query("select profile from Profile profile left join fetch profile.user")
    List<Profile> findAllWithToOneRelationships();

    @Query("select profile from Profile profile left join fetch profile.user where profile.id =:id")
    Optional<Profile> findOneWithToOneRelationships(@Param("id") Long id);

    Optional<Profile> findOneByUser_Id(Long id);
    Optional<Profile> findOneByUser_Login(String login);

    @Query(value = "select distinct p.* from profile p join account_preferences ap on ap.profile_id=p.id join generate_series(cast(:startDate as date), cast(:endDate as date), interval '1 day') d on extract(month from p.birth_date)=extract(month from d) and extract(day from p.birth_date)=extract(day from d) where p.birth_date is not null and p.status='ACTIVE' and ap.discoverable=true", nativeQuery = true)
    List<Profile> findBirthdaysInRange(@Param("startDate") java.time.LocalDate startDate, @Param("endDate") java.time.LocalDate endDate);

    boolean existsByUsernameIgnoreCase(String username);

    @Query("select p from Profile p where p.id <> :profileId and (lower(p.displayName) like lower(concat('%', :query, '%')) or lower(p.username) like lower(concat('%', :query, '%'))) order by p.displayName")
    Page<Profile> searchMessageRecipients(@Param("profileId") Long profileId, @Param("query") String query, Pageable pageable);
}
