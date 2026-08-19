package com.bialem.backend.service;

import com.bialem.backend.domain.*; // for static metamodels
import com.bialem.backend.domain.Report;
import com.bialem.backend.repository.ReportRepository;
import com.bialem.backend.service.criteria.ReportCriteria;
import com.bialem.backend.service.dto.ReportDTO;
import com.bialem.backend.service.mapper.ReportMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Report} entities in the database.
 * The main input is a {@link ReportCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ReportDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ReportQueryService extends QueryService<Report> {

    private static final Logger LOG = LoggerFactory.getLogger(ReportQueryService.class);

    private final ReportRepository reportRepository;

    private final ReportMapper reportMapper;

    public ReportQueryService(ReportRepository reportRepository, ReportMapper reportMapper) {
        this.reportRepository = reportRepository;
        this.reportMapper = reportMapper;
    }

    /**
     * Return a {@link List} of {@link ReportDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ReportDTO> findByCriteria(ReportCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Report> specification = createSpecification(criteria);
        return reportMapper.toDto(reportRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ReportCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Report> specification = createSpecification(criteria);
        return reportRepository.count(specification);
    }

    /**
     * Function to convert {@link ReportCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Report> createSpecification(ReportCriteria criteria) {
        Specification<Report> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Report_.id),
                buildSpecification(criteria.getTargetType(), Report_.targetType),
                buildSpecification(criteria.getTargetId(), Report_.targetId),
                buildStringSpecification(criteria.getReason(), Report_.reason),
                buildSpecification(criteria.getStatus(), Report_.status),
                buildRangeSpecification(criteria.getResolvedAt(), Report_.resolvedAt),
                buildRangeSpecification(criteria.getCreatedAt(), Report_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Report_.updatedAt),
                buildSpecification(criteria.getReporterId(), root -> root.join(Report_.reporter, JoinType.LEFT).get(Profile_.id)),
                buildSpecification(criteria.getResolvedById(), root -> root.join(Report_.resolvedBy, JoinType.LEFT).get(Profile_.id))
            );
        }
        return specification;
    }
}
