package com.bialem.backend.service;

import com.bialem.backend.domain.PostMedia;
import com.bialem.backend.repository.PostMediaRepository;
import com.bialem.backend.service.dto.PostMediaDTO;
import com.bialem.backend.service.mapper.PostMediaMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.PostMedia}.
 */
@Service
@Transactional
public class PostMediaService {

    private static final Logger LOG = LoggerFactory.getLogger(PostMediaService.class);

    private final PostMediaRepository postMediaRepository;

    private final PostMediaMapper postMediaMapper;

    public PostMediaService(PostMediaRepository postMediaRepository, PostMediaMapper postMediaMapper) {
        this.postMediaRepository = postMediaRepository;
        this.postMediaMapper = postMediaMapper;
    }

    /**
     * Save a postMedia.
     *
     * @param postMediaDTO the entity to save.
     * @return the persisted entity.
     */
    public PostMediaDTO save(PostMediaDTO postMediaDTO) {
        LOG.debug("Request to save PostMedia : {}", postMediaDTO);
        PostMedia postMedia = postMediaMapper.toEntity(postMediaDTO);
        postMedia = postMediaRepository.save(postMedia);
        return postMediaMapper.toDto(postMedia);
    }

    /**
     * Update a postMedia.
     *
     * @param postMediaDTO the entity to save.
     * @return the persisted entity.
     */
    public PostMediaDTO update(PostMediaDTO postMediaDTO) {
        LOG.debug("Request to update PostMedia : {}", postMediaDTO);
        PostMedia postMedia = postMediaMapper.toEntity(postMediaDTO);
        postMedia = postMediaRepository.save(postMedia);
        return postMediaMapper.toDto(postMedia);
    }

    /**
     * Partially update a postMedia.
     *
     * @param postMediaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PostMediaDTO> partialUpdate(PostMediaDTO postMediaDTO) {
        LOG.debug("Request to partially update PostMedia : {}", postMediaDTO);

        return postMediaRepository
            .findById(postMediaDTO.getId())
            .map(existingPostMedia -> {
                postMediaMapper.partialUpdate(existingPostMedia, postMediaDTO);

                return existingPostMedia;
            })
            .map(postMediaRepository::save)
            .map(postMediaMapper::toDto);
    }

    /**
     * Get all the postMedias.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PostMediaDTO> findAll() {
        LOG.debug("Request to get all PostMedias");
        return postMediaRepository.findAll().stream().map(postMediaMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one postMedia by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PostMediaDTO> findOne(Long id) {
        LOG.debug("Request to get PostMedia : {}", id);
        return postMediaRepository.findById(id).map(postMediaMapper::toDto);
    }

    /**
     * Delete the postMedia by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PostMedia : {}", id);
        postMediaRepository.deleteById(id);
    }
}
