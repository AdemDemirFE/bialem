package com.bialem.backend.service;

import com.bialem.backend.domain.RadioContent;
import com.bialem.backend.domain.enumeration.RadioContentType;
import com.bialem.backend.repository.RadioContentRepository;
import com.bialem.backend.service.dto.RadioContentDTO;
import com.bialem.backend.service.mapper.RadioContentMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link RadioContent}.
 */
@Service
@Transactional
public class RadioContentService {

    private final Logger log = LoggerFactory.getLogger(RadioContentService.class);

    private final RadioContentRepository radioContentRepository;

    private final RadioContentMapper radioContentMapper;

    public RadioContentService(RadioContentRepository radioContentRepository, RadioContentMapper radioContentMapper) {
        this.radioContentRepository = radioContentRepository;
        this.radioContentMapper = radioContentMapper;
    }

    /**
     * Save a radioContent.
     *
     * @param radioContentDTO the entity to save.
     * @return the persisted entity.
     */
    public RadioContentDTO save(RadioContentDTO radioContentDTO) {
        log.debug("Request to save RadioContent : {}", radioContentDTO);
        RadioContent radioContent = radioContentMapper.toEntity(radioContentDTO);
        if (radioContent.getCreatedAt() == null) {
            radioContent.setCreatedAt(Instant.now());
        }
        radioContent.setUpdatedAt(Instant.now());
        if (radioContent.getPlayCount() == null) {
            radioContent.setPlayCount(0L);
        }
        radioContent = radioContentRepository.save(radioContent);
        return radioContentMapper.toDto(radioContent);
    }

    /**
     * Partially update a radioContent.
     *
     * @param radioContentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RadioContentDTO> partialUpdate(RadioContentDTO radioContentDTO) {
        log.debug("Request to partially update RadioContent : {}", radioContentDTO);

        return radioContentRepository
            .findById(radioContentDTO.getId())
            .map(existingRadioContent -> {
                radioContentMapper.partialUpdate(existingRadioContent, radioContentDTO);
                existingRadioContent.setUpdatedAt(Instant.now());
                return existingRadioContent;
            })
            .map(radioContentRepository::save)
            .map(radioContentMapper::toDto);
    }

    /**
     * Get all the radioContents.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<RadioContentDTO> findAll() {
        log.debug("Request to get all RadioContents");
        return radioContentMapper.toDto(
            radioContentRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder", "createdAt"))
        );
    }

    /**
     * Get one radioContent by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RadioContentDTO> findOne(Long id) {
        log.debug("Request to get RadioContent : {}", id);
        return radioContentRepository.findById(id).map(radioContentMapper::toDto);
    }

    /**
     * Delete the radioContent by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete RadioContent : {}", id);
        radioContentRepository.deleteById(id);
    }

    /**
     * Get all active radioContents.
     */
    @Transactional(readOnly = true)
    public List<RadioContentDTO> findAllActive() {
        return radioContentMapper.toDto(radioContentRepository.findByIsActiveTrueOrderBySortOrderAsc());
    }

    /**
     * Get all featured active radioContents.
     */
    @Transactional(readOnly = true)
    public List<RadioContentDTO> findAllFeatured() {
        return radioContentMapper.toDto(radioContentRepository.findByIsFeaturedTrueAndIsActiveTrueOrderBySortOrderAsc());
    }

    /**
     * Get radioContents by content type (active only).
     */
    @Transactional(readOnly = true)
    public List<RadioContentDTO> findByContentType(RadioContentType contentType) {
        return radioContentMapper.toDto(radioContentRepository.findByContentTypeAndIsActiveTrue(contentType));
    }

    /**
     * Get radioContents by category (active only).
     */
    @Transactional(readOnly = true)
    public List<RadioContentDTO> findByCategory(String category) {
        return radioContentMapper.toDto(radioContentRepository.findByCategoryAndIsActiveTrueOrderBySortOrderAsc(category));
    }

    /**
     * Get all distinct active categories.
     */
    @Transactional(readOnly = true)
    public List<String> findDistinctCategories() {
        return radioContentRepository.findDistinctCategories();
    }

    /**
     * Increment play count for a radioContent.
     */
    public void incrementPlayCount(Long id) {
        radioContentRepository
            .findById(id)
            .ifPresent(rc -> {
                rc.setPlayCount((rc.getPlayCount() == null ? 0L : rc.getPlayCount()) + 1);
                rc.setUpdatedAt(Instant.now());
                radioContentRepository.save(rc);
            });
    }
}
