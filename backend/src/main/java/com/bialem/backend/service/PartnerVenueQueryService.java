package com.bialem.backend.service;

import com.bialem.backend.domain.*; // for static metamodels
import com.bialem.backend.domain.PartnerVenue;
import com.bialem.backend.repository.PartnerVenueRepository;
import com.bialem.backend.service.criteria.PartnerVenueCriteria;
import com.bialem.backend.service.dto.PartnerVenueDTO;
import com.bialem.backend.service.mapper.PartnerVenueMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link PartnerVenue} entities in the database.
 * The main input is a {@link PartnerVenueCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PartnerVenueDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PartnerVenueQueryService extends QueryService<PartnerVenue> {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerVenueQueryService.class);

    private final PartnerVenueRepository partnerVenueRepository;

    private final PartnerVenueMapper partnerVenueMapper;

    public PartnerVenueQueryService(PartnerVenueRepository partnerVenueRepository, PartnerVenueMapper partnerVenueMapper) {
        this.partnerVenueRepository = partnerVenueRepository;
        this.partnerVenueMapper = partnerVenueMapper;
    }

    /**
     * Return a {@link List} of {@link PartnerVenueDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PartnerVenueDTO> findByCriteria(PartnerVenueCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<PartnerVenue> specification = createSpecification(criteria);
        return partnerVenueMapper.toDto(partnerVenueRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PartnerVenueCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<PartnerVenue> specification = createSpecification(criteria);
        return partnerVenueRepository.count(specification);
    }

    /**
     * Function to convert {@link PartnerVenueCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PartnerVenue> createSpecification(PartnerVenueCriteria criteria) {
        Specification<PartnerVenue> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), PartnerVenue_.id),
                buildStringSpecification(criteria.getName(), PartnerVenue_.name),
                buildStringSpecification(criteria.getSlug(), PartnerVenue_.slug),
                buildSpecification(criteria.getCategory(), PartnerVenue_.category),
                buildStringSpecification(criteria.getLogoUrl(), PartnerVenue_.logoUrl),
                buildStringSpecification(criteria.getCoverImageUrl(), PartnerVenue_.coverImageUrl),
                buildStringSpecification(criteria.getAddress(), PartnerVenue_.address),
                buildStringSpecification(criteria.getCity(), PartnerVenue_.city),
                buildRangeSpecification(criteria.getLatitude(), PartnerVenue_.latitude),
                buildRangeSpecification(criteria.getLongitude(), PartnerVenue_.longitude),
                buildStringSpecification(criteria.getPhone(), PartnerVenue_.phone),
                buildStringSpecification(criteria.getWebsiteUrl(), PartnerVenue_.websiteUrl),
                buildStringSpecification(criteria.getInstagramUrl(), PartnerVenue_.instagramUrl),
                buildSpecification(criteria.getIsFeatured(), PartnerVenue_.isFeatured),
                buildSpecification(criteria.getIsActive(), PartnerVenue_.isActive),
                buildRangeSpecification(criteria.getCreatedAt(), PartnerVenue_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), PartnerVenue_.updatedAt),
                buildSpecification(criteria.getOffersId(), root -> root.join(PartnerVenue_.offers, JoinType.LEFT).get(PartnerOffer_.id)),
                buildSpecification(criteria.getStaffId(), root -> root.join(PartnerVenue_.staff, JoinType.LEFT).get(PartnerVenueStaff_.id))
            );
        }
        return specification;
    }
}
