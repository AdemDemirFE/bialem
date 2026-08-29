package com.bialem.backend.repository;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.UserRole;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserRole entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUser(Profile user);
}
