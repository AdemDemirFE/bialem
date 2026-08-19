package com.bialem.backend.service;

import com.bialem.backend.domain.StoryView;
import com.bialem.backend.repository.StoryViewRepository;
import com.bialem.backend.service.dto.StoryViewDTO;
import com.bialem.backend.service.mapper.StoryViewMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.StoryView}.
 */
@Service
@Transactional
public class StoryViewService {

    private static final Logger LOG = LoggerFactory.getLogger(StoryViewService.class);

    private final StoryViewRepository storyViewRepository;

    private final StoryViewMapper storyViewMapper;

    public StoryViewService(StoryViewRepository storyViewRepository, StoryViewMapper storyViewMapper) {
        this.storyViewRepository = storyViewRepository;
        this.storyViewMapper = storyViewMapper;
    }

    /**
     * Save a storyView.
     *
     * @param storyViewDTO the entity to save.
     * @return the persisted entity.
     */
    public StoryViewDTO save(StoryViewDTO storyViewDTO) {
        LOG.debug("Request to save StoryView : {}", storyViewDTO);
        StoryView storyView = storyViewMapper.toEntity(storyViewDTO);
        storyView = storyViewRepository.save(storyView);
        return storyViewMapper.toDto(storyView);
    }

    /**
     * Update a storyView.
     *
     * @param storyViewDTO the entity to save.
     * @return the persisted entity.
     */
    public StoryViewDTO update(StoryViewDTO storyViewDTO) {
        LOG.debug("Request to update StoryView : {}", storyViewDTO);
        StoryView storyView = storyViewMapper.toEntity(storyViewDTO);
        storyView = storyViewRepository.save(storyView);
        return storyViewMapper.toDto(storyView);
    }

    /**
     * Partially update a storyView.
     *
     * @param storyViewDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StoryViewDTO> partialUpdate(StoryViewDTO storyViewDTO) {
        LOG.debug("Request to partially update StoryView : {}", storyViewDTO);

        return storyViewRepository
            .findById(storyViewDTO.getId())
            .map(existingStoryView -> {
                storyViewMapper.partialUpdate(existingStoryView, storyViewDTO);

                return existingStoryView;
            })
            .map(storyViewRepository::save)
            .map(storyViewMapper::toDto);
    }

    /**
     * Get all the storyViews.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<StoryViewDTO> findAll() {
        LOG.debug("Request to get all StoryViews");
        return storyViewRepository.findAll().stream().map(storyViewMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one storyView by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StoryViewDTO> findOne(Long id) {
        LOG.debug("Request to get StoryView : {}", id);
        return storyViewRepository.findById(id).map(storyViewMapper::toDto);
    }

    /**
     * Delete the storyView by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete StoryView : {}", id);
        storyViewRepository.deleteById(id);
    }
}
