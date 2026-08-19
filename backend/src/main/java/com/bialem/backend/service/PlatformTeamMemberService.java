package com.bialem.backend.service;

import com.bialem.backend.domain.PlatformTeamMember;
import com.bialem.backend.repository.PlatformTeamMemberRepository;
import com.bialem.backend.service.dto.PlatformTeamMemberDTO;
import com.bialem.backend.service.mapper.PlatformTeamMemberMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.PlatformTeamMember}.
 */
@Service
@Transactional
public class PlatformTeamMemberService {

    private static final Logger LOG = LoggerFactory.getLogger(PlatformTeamMemberService.class);

    private final PlatformTeamMemberRepository platformTeamMemberRepository;

    private final PlatformTeamMemberMapper platformTeamMemberMapper;

    public PlatformTeamMemberService(
        PlatformTeamMemberRepository platformTeamMemberRepository,
        PlatformTeamMemberMapper platformTeamMemberMapper
    ) {
        this.platformTeamMemberRepository = platformTeamMemberRepository;
        this.platformTeamMemberMapper = platformTeamMemberMapper;
    }

    /**
     * Save a platformTeamMember.
     *
     * @param platformTeamMemberDTO the entity to save.
     * @return the persisted entity.
     */
    public PlatformTeamMemberDTO save(PlatformTeamMemberDTO platformTeamMemberDTO) {
        LOG.debug("Request to save PlatformTeamMember : {}", platformTeamMemberDTO);
        PlatformTeamMember platformTeamMember = platformTeamMemberMapper.toEntity(platformTeamMemberDTO);
        platformTeamMember = platformTeamMemberRepository.save(platformTeamMember);
        return platformTeamMemberMapper.toDto(platformTeamMember);
    }

    /**
     * Update a platformTeamMember.
     *
     * @param platformTeamMemberDTO the entity to save.
     * @return the persisted entity.
     */
    public PlatformTeamMemberDTO update(PlatformTeamMemberDTO platformTeamMemberDTO) {
        LOG.debug("Request to update PlatformTeamMember : {}", platformTeamMemberDTO);
        PlatformTeamMember platformTeamMember = platformTeamMemberMapper.toEntity(platformTeamMemberDTO);
        platformTeamMember = platformTeamMemberRepository.save(platformTeamMember);
        return platformTeamMemberMapper.toDto(platformTeamMember);
    }

    /**
     * Partially update a platformTeamMember.
     *
     * @param platformTeamMemberDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PlatformTeamMemberDTO> partialUpdate(PlatformTeamMemberDTO platformTeamMemberDTO) {
        LOG.debug("Request to partially update PlatformTeamMember : {}", platformTeamMemberDTO);

        return platformTeamMemberRepository
            .findById(platformTeamMemberDTO.getId())
            .map(existingPlatformTeamMember -> {
                platformTeamMemberMapper.partialUpdate(existingPlatformTeamMember, platformTeamMemberDTO);

                return existingPlatformTeamMember;
            })
            .map(platformTeamMemberRepository::save)
            .map(platformTeamMemberMapper::toDto);
    }

    /**
     * Get all the platformTeamMembers.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PlatformTeamMemberDTO> findAll() {
        LOG.debug("Request to get all PlatformTeamMembers");
        return platformTeamMemberRepository
            .findAll()
            .stream()
            .map(platformTeamMemberMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one platformTeamMember by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PlatformTeamMemberDTO> findOne(Long id) {
        LOG.debug("Request to get PlatformTeamMember : {}", id);
        return platformTeamMemberRepository.findById(id).map(platformTeamMemberMapper::toDto);
    }

    /**
     * Delete the platformTeamMember by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PlatformTeamMember : {}", id);
        platformTeamMemberRepository.deleteById(id);
    }
}
