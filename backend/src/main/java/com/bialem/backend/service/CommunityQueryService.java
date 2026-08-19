package com.bialem.backend.service;

import com.bialem.backend.domain.*; // for static metamodels
import com.bialem.backend.domain.Community;
import com.bialem.backend.repository.CommunityRepository;
import com.bialem.backend.service.criteria.CommunityCriteria;
import com.bialem.backend.service.dto.CommunityDTO;
import com.bialem.backend.service.mapper.CommunityMapper;
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
 * Service for executing complex queries for {@link Community} entities in the database.
 * The main input is a {@link CommunityCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CommunityDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CommunityQueryService extends QueryService<Community> {

    private static final Logger LOG = LoggerFactory.getLogger(CommunityQueryService.class);

    private final CommunityRepository communityRepository;

    private final CommunityMapper communityMapper;

    public CommunityQueryService(CommunityRepository communityRepository, CommunityMapper communityMapper) {
        this.communityRepository = communityRepository;
        this.communityMapper = communityMapper;
    }

    /**
     * Return a {@link Page} of {@link CommunityDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CommunityDTO> findByCriteria(CommunityCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Community> specification = createSpecification(criteria);
        return communityRepository.findAll(specification, page).map(communityMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CommunityCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Community> specification = createSpecification(criteria);
        return communityRepository.count(specification);
    }

    /**
     * Function to convert {@link CommunityCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Community> createSpecification(CommunityCriteria criteria) {
        Specification<Community> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Community_.id),
                buildStringSpecification(criteria.getName(), Community_.name),
                buildStringSpecification(criteria.getSlug(), Community_.slug),
                buildSpecification(criteria.getVisibility(), Community_.visibility),
                buildStringSpecification(criteria.getCoverImageUrl(), Community_.coverImageUrl),
                buildSpecification(criteria.getCommunityType(), Community_.communityType),
                buildSpecification(criteria.getPartnerTrustLevel(), Community_.partnerTrustLevel),
                buildSpecification(criteria.getIsVerifiedPartner(), Community_.isVerifiedPartner),
                buildSpecification(criteria.getIsDiscoverable(), Community_.isDiscoverable),
                buildRangeSpecification(criteria.getCreatedAt(), Community_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Community_.updatedAt),
                buildSpecification(criteria.getParentId(), root -> root.join(Community_.parent, JoinType.LEFT).get(Community_.id)),
                buildSpecification(criteria.getCategoryHubId(), root -> root.join(Community_.categoryHub, JoinType.LEFT).get(Community_.id)
                ),
                buildSpecification(criteria.getCreatedById(), root -> root.join(Community_.createdBy, JoinType.LEFT).get(Profile_.id)),
                buildSpecification(criteria.getLeadModeratorId(), root ->
                    root.join(Community_.leadModerator, JoinType.LEFT).get(Profile_.id)
                ),
                buildSpecification(criteria.getChildrenId(), root -> root.join(Community_.children, JoinType.LEFT).get(Community_.id)),
                buildSpecification(criteria.getCategorizedGroupsId(), root ->
                    root.join(Community_.categorizedGroups, JoinType.LEFT).get(Community_.id)
                ),
                buildSpecification(criteria.getMembersId(), root -> root.join(Community_.members, JoinType.LEFT).get(CommunityMember_.id)),
                buildSpecification(criteria.getAssistantsId(), root ->
                    root.join(Community_.assistants, JoinType.LEFT).get(CommunityModeratorAssistant_.id)
                ),
                buildSpecification(criteria.getEventsId(), root -> root.join(Community_.events, JoinType.LEFT).get(Event_.id)),
                buildSpecification(criteria.getPostsId(), root -> root.join(Community_.posts, JoinType.LEFT).get(Post_.id)),
                buildSpecification(criteria.getStoryTargetsId(), root ->
                    root.join(Community_.storyTargets, JoinType.LEFT).get(StoryCommunityTarget_.id)
                )
            );
        }
        return specification;
    }
}
