package com.bialem.backend.service;

import com.bialem.backend.domain.StoryReaction;
import com.bialem.backend.repository.StoryReactionRepository;
import com.bialem.backend.service.dto.StoryReactionDTO;
import com.bialem.backend.service.mapper.StoryReactionMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.StoryReaction}.
 */
@Service
@Transactional
public class StoryReactionService {

    private static final Logger LOG = LoggerFactory.getLogger(StoryReactionService.class);

    private final StoryReactionRepository storyReactionRepository;

    private final StoryReactionMapper storyReactionMapper;

    public StoryReactionService(StoryReactionRepository storyReactionRepository, StoryReactionMapper storyReactionMapper) {
        this.storyReactionRepository = storyReactionRepository;
        this.storyReactionMapper = storyReactionMapper;
    }

    public StoryReactionDTO save(StoryReactionDTO storyReactionDTO) {
        LOG.debug("Request to save StoryReaction : {}", storyReactionDTO);
        StoryReaction storyReaction = storyReactionMapper.toEntity(storyReactionDTO);
        storyReaction = storyReactionRepository.save(storyReaction);
        return storyReactionMapper.toDto(storyReaction);
    }

    public StoryReactionDTO update(StoryReactionDTO storyReactionDTO) {
        LOG.debug("Request to update StoryReaction : {}", storyReactionDTO);
        StoryReaction storyReaction = storyReactionMapper.toEntity(storyReactionDTO);
        storyReaction = storyReactionRepository.save(storyReaction);
        return storyReactionMapper.toDto(storyReaction);
    }

    public Optional<StoryReactionDTO> partialUpdate(StoryReactionDTO storyReactionDTO) {
        LOG.debug("Request to partially update StoryReaction : {}", storyReactionDTO);
        return storyReactionRepository
            .findById(storyReactionDTO.getId())
            .map(existingStoryReaction -> {
                storyReactionMapper.partialUpdate(existingStoryReaction, storyReactionDTO);
                return existingStoryReaction;
            })
            .map(storyReactionRepository::save)
            .map(storyReactionMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<StoryReactionDTO> findAll() {
        LOG.debug("Request to get all StoryReactions");
        return storyReactionRepository.findAll().stream().map(storyReactionMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Transactional(readOnly = true)
    public Optional<StoryReactionDTO> findOne(Long id) {
        LOG.debug("Request to get StoryReaction : {}", id);
        return storyReactionRepository.findById(id).map(storyReactionMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete StoryReaction : {}", id);
        storyReactionRepository.deleteById(id);
    }
}
