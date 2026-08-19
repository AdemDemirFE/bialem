package com.bialem.backend.service;

import com.bialem.backend.domain.*; // for static metamodels
import com.bialem.backend.domain.Profile;
import com.bialem.backend.repository.ProfileRepository;
import com.bialem.backend.service.criteria.ProfileCriteria;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.mapper.ProfileMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Profile} entities in the database.
 * The main input is a {@link ProfileCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ProfileDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProfileQueryService extends QueryService<Profile> {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileQueryService.class);

    private final ProfileRepository profileRepository;

    private final ProfileMapper profileMapper;

    public ProfileQueryService(ProfileRepository profileRepository, ProfileMapper profileMapper) {
        this.profileRepository = profileRepository;
        this.profileMapper = profileMapper;
    }

    /**
     * Return a {@link Page} of {@link ProfileDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProfileDTO> findByCriteria(ProfileCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Profile> specification = createSpecification(criteria);
        return profileRepository.findAll(specification, page).map(profileMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProfileCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Profile> specification = createSpecification(criteria);
        return profileRepository.count(specification);
    }

    /**
     * Function to convert {@link ProfileCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Profile> createSpecification(ProfileCriteria criteria) {
        Specification<Profile> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Profile_.id),
                buildStringSpecification(criteria.getDisplayName(), Profile_.displayName),
                buildStringSpecification(criteria.getUsername(), Profile_.username),
                buildStringSpecification(criteria.getAvatarUrl(), Profile_.avatarUrl),
                buildStringSpecification(criteria.getBio(), Profile_.bio),
                buildStringSpecification(criteria.getCity(), Profile_.city),
                buildSpecification(criteria.getStatus(), Profile_.status),
                buildSpecification(criteria.getIsVerified(), Profile_.isVerified),
                buildRangeSpecification(criteria.getCreatedAt(), Profile_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Profile_.updatedAt),
                buildSpecification(criteria.getUserId(), root -> root.join(Profile_.user, JoinType.LEFT).get(User_.id)),
                buildSpecification(criteria.getPreferencesId(), root ->
                    root.join(Profile_.preferences, JoinType.LEFT).get(AccountPreferences_.id)
                )
            );
        }
        return specification;
    }
}
