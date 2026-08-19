package com.bialem.backend.service;

import com.bialem.backend.domain.Story;
import com.bialem.backend.repository.StoryCommunityTargetRepository;
import com.bialem.backend.repository.StoryRepository;
import com.bialem.backend.repository.StoryViewRepository;
import com.bialem.backend.service.dto.StoryDTO;
import com.bialem.backend.service.mapper.StoryMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.Story}.
 */
@Service
@Transactional
public class StoryService {

    private static final Logger LOG = LoggerFactory.getLogger(StoryService.class);

    private final StoryRepository storyRepository;
    private final StoryViewRepository storyViewRepository;
    private final StoryCommunityTargetRepository storyCommunityTargetRepository;
    private final StoryMapper storyMapper;

    public StoryService(
        StoryRepository storyRepository,
        StoryViewRepository storyViewRepository,
        StoryCommunityTargetRepository storyCommunityTargetRepository,
        StoryMapper storyMapper
    ) {
        this.storyRepository = storyRepository;
        this.storyViewRepository = storyViewRepository;
        this.storyCommunityTargetRepository = storyCommunityTargetRepository;
        this.storyMapper = storyMapper;
    }

    /**
     * Save a story.
     *
     * @param storyDTO the entity to save.
     * @return the persisted entity.
     */
    public StoryDTO save(StoryDTO storyDTO) {
        LOG.debug("Request to save Story : {}", storyDTO);
        Story story = storyMapper.toEntity(storyDTO);
        story = storyRepository.save(story);
        return storyMapper.toDto(story);
    }

    /**
     * Update a story.
     *
     * @param storyDTO the entity to save.
     * @return the persisted entity.
     */
    public StoryDTO update(StoryDTO storyDTO) {
        LOG.debug("Request to update Story : {}", storyDTO);
        Story story = storyMapper.toEntity(storyDTO);
        story = storyRepository.save(story);
        return storyMapper.toDto(story);
    }

    /**
     * Partially update a story.
     *
     * @param storyDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StoryDTO> partialUpdate(StoryDTO storyDTO) {
        LOG.debug("Request to partially update Story : {}", storyDTO);

        return storyRepository
            .findById(storyDTO.getId())
            .map(existingStory -> {
                storyMapper.partialUpdate(existingStory, storyDTO);

                return existingStory;
            })
            .map(storyRepository::save)
            .map(storyMapper::toDto);
    }

    /**
     * Get all the stories.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<StoryDTO> findAll() {
        LOG.debug("Request to get all Stories");
        return storyRepository.findAll().stream().map(storyMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one story by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StoryDTO> findOne(Long id) {
        LOG.debug("Request to get Story : {}", id);
        return storyRepository.findById(id).map(storyMapper::toDto);
    }

    /**
     * Delete the story by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Story : {}", id);
        storyViewRepository.deleteByStory_Id(id);
        storyCommunityTargetRepository.deleteByStory_Id(id);
        storyRepository.deleteById(id);
    }
}
