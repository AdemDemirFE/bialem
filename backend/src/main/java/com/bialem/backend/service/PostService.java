package com.bialem.backend.service;

import com.bialem.backend.domain.Post;
import com.bialem.backend.repository.PostRepository;
import com.bialem.backend.service.dto.PostDTO;
import com.bialem.backend.service.mapper.PostMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.Post}.
 */
@Service
@Transactional
public class PostService {

    private static final Logger LOG = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;

    private final PostMapper postMapper;

    public PostService(PostRepository postRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }

    /**
     * Save a post.
     *
     * @param postDTO the entity to save.
     * @return the persisted entity.
     */
    public PostDTO save(PostDTO postDTO) {
        LOG.debug("Request to save Post : {}", postDTO);
        Post post = postMapper.toEntity(postDTO);
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    /**
     * Update a post.
     *
     * @param postDTO the entity to save.
     * @return the persisted entity.
     */
    public PostDTO update(PostDTO postDTO) {
        LOG.debug("Request to update Post : {}", postDTO);
        Post post = postMapper.toEntity(postDTO);
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    /**
     * Partially update a post.
     *
     * @param postDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PostDTO> partialUpdate(PostDTO postDTO) {
        LOG.debug("Request to partially update Post : {}", postDTO);

        return postRepository
            .findById(postDTO.getId())
            .map(existingPost -> {
                postMapper.partialUpdate(existingPost, postDTO);

                return existingPost;
            })
            .map(postRepository::save)
            .map(postMapper::toDto);
    }

    /**
     * Get all the posts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PostDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Posts");
        return postRepository.findAll(pageable).map(postMapper::toDto);
    }

    /**
     * Get all the posts filtered by author, community, or event.
     *
     * @param authorId optional filter by author profile id.
     * @param communityId optional filter by community id.
     * @param eventId optional filter by event id.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PostDTO> findAll(Long authorId, Long communityId, Long eventId, Pageable pageable) {
        LOG.debug("Request to get all Posts by authorId {}, communityId {}, eventId {}", authorId, communityId, eventId);
        if (authorId == null && communityId == null && eventId == null) {
            return postRepository.findAll(pageable).map(postMapper::toDto);
        }
        return postRepository.findAllWithRelationshipsByFilters(authorId, communityId, eventId, pageable).map(postMapper::toDto);
    }

    /**
     * Count posts filtered by author, community, or event.
     *
     * @param authorId optional filter by author profile id.
     * @param communityId optional filter by community id.
     * @param eventId optional filter by event id.
     * @return the count.
     */
    @Transactional(readOnly = true)
    public long count(Long authorId, Long communityId, Long eventId) {
        LOG.debug("Request to count Posts by authorId {}, communityId {}, eventId {}", authorId, communityId, eventId);
        if (authorId == null && communityId == null && eventId == null) {
            return postRepository.count();
        }
        return postRepository.countByFilters(authorId, communityId, eventId);
    }

    /**
     * Get one post by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PostDTO> findOne(Long id) {
        LOG.debug("Request to get Post : {}", id);
        return postRepository.findById(id).map(postMapper::toDto);
    }

    /**
     * Delete the post by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Post : {}", id);
        postRepository.deleteById(id);
    }
}
