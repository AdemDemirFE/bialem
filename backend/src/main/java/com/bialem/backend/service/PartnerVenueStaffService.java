package com.bialem.backend.service;

import com.bialem.backend.domain.PartnerVenueStaff;
import com.bialem.backend.repository.PartnerVenueStaffRepository;
import com.bialem.backend.service.dto.PartnerVenueStaffDTO;
import com.bialem.backend.service.mapper.PartnerVenueStaffMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.PartnerVenueStaff}.
 */
@Service
@Transactional
public class PartnerVenueStaffService {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerVenueStaffService.class);

    private final PartnerVenueStaffRepository partnerVenueStaffRepository;

    private final PartnerVenueStaffMapper partnerVenueStaffMapper;

    public PartnerVenueStaffService(
        PartnerVenueStaffRepository partnerVenueStaffRepository,
        PartnerVenueStaffMapper partnerVenueStaffMapper
    ) {
        this.partnerVenueStaffRepository = partnerVenueStaffRepository;
        this.partnerVenueStaffMapper = partnerVenueStaffMapper;
    }

    /**
     * Save a partnerVenueStaff.
     *
     * @param partnerVenueStaffDTO the entity to save.
     * @return the persisted entity.
     */
    public PartnerVenueStaffDTO save(PartnerVenueStaffDTO partnerVenueStaffDTO) {
        LOG.debug("Request to save PartnerVenueStaff : {}", partnerVenueStaffDTO);
        PartnerVenueStaff partnerVenueStaff = partnerVenueStaffMapper.toEntity(partnerVenueStaffDTO);
        partnerVenueStaff = partnerVenueStaffRepository.save(partnerVenueStaff);
        return partnerVenueStaffMapper.toDto(partnerVenueStaff);
    }

    /**
     * Update a partnerVenueStaff.
     *
     * @param partnerVenueStaffDTO the entity to save.
     * @return the persisted entity.
     */
    public PartnerVenueStaffDTO update(PartnerVenueStaffDTO partnerVenueStaffDTO) {
        LOG.debug("Request to update PartnerVenueStaff : {}", partnerVenueStaffDTO);
        PartnerVenueStaff partnerVenueStaff = partnerVenueStaffMapper.toEntity(partnerVenueStaffDTO);
        partnerVenueStaff = partnerVenueStaffRepository.save(partnerVenueStaff);
        return partnerVenueStaffMapper.toDto(partnerVenueStaff);
    }

    /**
     * Partially update a partnerVenueStaff.
     *
     * @param partnerVenueStaffDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PartnerVenueStaffDTO> partialUpdate(PartnerVenueStaffDTO partnerVenueStaffDTO) {
        LOG.debug("Request to partially update PartnerVenueStaff : {}", partnerVenueStaffDTO);

        return partnerVenueStaffRepository
            .findById(partnerVenueStaffDTO.getId())
            .map(existingPartnerVenueStaff -> {
                partnerVenueStaffMapper.partialUpdate(existingPartnerVenueStaff, partnerVenueStaffDTO);

                return existingPartnerVenueStaff;
            })
            .map(partnerVenueStaffRepository::save)
            .map(partnerVenueStaffMapper::toDto);
    }

    /**
     * Get all the partnerVenueStaffs.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PartnerVenueStaffDTO> findAll() {
        LOG.debug("Request to get all PartnerVenueStaffs");
        return partnerVenueStaffRepository
            .findAll()
            .stream()
            .map(partnerVenueStaffMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one partnerVenueStaff by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PartnerVenueStaffDTO> findOne(Long id) {
        LOG.debug("Request to get PartnerVenueStaff : {}", id);
        return partnerVenueStaffRepository.findById(id).map(partnerVenueStaffMapper::toDto);
    }

    /**
     * Delete the partnerVenueStaff by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PartnerVenueStaff : {}", id);
        partnerVenueStaffRepository.deleteById(id);
    }
}
