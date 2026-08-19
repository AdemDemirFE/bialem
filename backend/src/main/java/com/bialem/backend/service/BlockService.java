package com.bialem.backend.service;

import com.bialem.backend.domain.Block;
import com.bialem.backend.repository.BlockRepository;
import com.bialem.backend.service.dto.BlockDTO;
import com.bialem.backend.service.mapper.BlockMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.Block}.
 */
@Service
@Transactional
public class BlockService {

    private static final Logger LOG = LoggerFactory.getLogger(BlockService.class);

    private final BlockRepository blockRepository;

    private final BlockMapper blockMapper;

    public BlockService(BlockRepository blockRepository, BlockMapper blockMapper) {
        this.blockRepository = blockRepository;
        this.blockMapper = blockMapper;
    }

    /**
     * Save a block.
     *
     * @param blockDTO the entity to save.
     * @return the persisted entity.
     */
    public BlockDTO save(BlockDTO blockDTO) {
        LOG.debug("Request to save Block : {}", blockDTO);
        Block block = blockMapper.toEntity(blockDTO);
        block = blockRepository.save(block);
        return blockMapper.toDto(block);
    }

    /**
     * Update a block.
     *
     * @param blockDTO the entity to save.
     * @return the persisted entity.
     */
    public BlockDTO update(BlockDTO blockDTO) {
        LOG.debug("Request to update Block : {}", blockDTO);
        Block block = blockMapper.toEntity(blockDTO);
        block = blockRepository.save(block);
        return blockMapper.toDto(block);
    }

    /**
     * Partially update a block.
     *
     * @param blockDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BlockDTO> partialUpdate(BlockDTO blockDTO) {
        LOG.debug("Request to partially update Block : {}", blockDTO);

        return blockRepository
            .findById(blockDTO.getId())
            .map(existingBlock -> {
                blockMapper.partialUpdate(existingBlock, blockDTO);

                return existingBlock;
            })
            .map(blockRepository::save)
            .map(blockMapper::toDto);
    }

    /**
     * Get all the blocks.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BlockDTO> findAll() {
        LOG.debug("Request to get all Blocks");
        return blockRepository.findAll().stream().map(blockMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one block by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BlockDTO> findOne(Long id) {
        LOG.debug("Request to get Block : {}", id);
        return blockRepository.findById(id).map(blockMapper::toDto);
    }

    /**
     * Delete the block by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Block : {}", id);
        blockRepository.deleteById(id);
    }
}
