package com.bialem.backend.service;

import com.bialem.backend.domain.CommunityMember;
import com.bialem.backend.repository.CommunityMemberRepository;
import com.bialem.backend.service.dto.CommunityMemberDTO;
import com.bialem.backend.service.mapper.CommunityMemberMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.CommunityMember}.
 */
@Service
@Transactional
public class CommunityMemberService {

    private static final Logger LOG = LoggerFactory.getLogger(CommunityMemberService.class);

    private final CommunityMemberRepository communityMemberRepository;

    private final CommunityMemberMapper communityMemberMapper;

    public CommunityMemberService(CommunityMemberRepository communityMemberRepository, CommunityMemberMapper communityMemberMapper) {
        this.communityMemberRepository = communityMemberRepository;
        this.communityMemberMapper = communityMemberMapper;
    }

    /**
     * Save a communityMember.
     *
     * @param communityMemberDTO the entity to save.
     * @return the persisted entity.
     */
    public CommunityMemberDTO save(CommunityMemberDTO communityMemberDTO) {
        LOG.debug("Request to save CommunityMember : {}", communityMemberDTO);
        CommunityMember communityMember = communityMemberMapper.toEntity(communityMemberDTO);
        communityMember = communityMemberRepository.save(communityMember);
        return communityMemberMapper.toDto(communityMember);
    }

    /**
     * Update a communityMember.
     *
     * @param communityMemberDTO the entity to save.
     * @return the persisted entity.
     */
    public CommunityMemberDTO update(CommunityMemberDTO communityMemberDTO) {
        LOG.debug("Request to update CommunityMember : {}", communityMemberDTO);
        CommunityMember communityMember = communityMemberMapper.toEntity(communityMemberDTO);
        communityMember = communityMemberRepository.save(communityMember);
        return communityMemberMapper.toDto(communityMember);
    }

    /**
     * Partially update a communityMember.
     *
     * @param communityMemberDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CommunityMemberDTO> partialUpdate(CommunityMemberDTO communityMemberDTO) {
        LOG.debug("Request to partially update CommunityMember : {}", communityMemberDTO);

        return communityMemberRepository
            .findById(communityMemberDTO.getId())
            .map(existingCommunityMember -> {
                communityMemberMapper.partialUpdate(existingCommunityMember, communityMemberDTO);

                return existingCommunityMember;
            })
            .map(communityMemberRepository::save)
            .map(communityMemberMapper::toDto);
    }

    /**
     * Get all the communityMembers.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CommunityMemberDTO> findAll() {
        LOG.debug("Request to get all CommunityMembers");
        return communityMemberRepository
            .findAll()
            .stream()
            .map(communityMemberMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one communityMember by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CommunityMemberDTO> findOne(Long id) {
        LOG.debug("Request to get CommunityMember : {}", id);
        return communityMemberRepository.findById(id).map(communityMemberMapper::toDto);
    }

    /**
     * Delete the communityMember by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CommunityMember : {}", id);
        communityMemberRepository.deleteById(id);
    }
}
