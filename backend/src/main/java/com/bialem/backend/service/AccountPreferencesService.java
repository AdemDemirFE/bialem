package com.bialem.backend.service;

import com.bialem.backend.domain.AccountPreferences;
import com.bialem.backend.repository.AccountPreferencesRepository;
import com.bialem.backend.service.dto.AccountPreferencesDTO;
import com.bialem.backend.service.mapper.AccountPreferencesMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.AccountPreferences}.
 */
@Service
@Transactional
public class AccountPreferencesService {

    private static final Logger LOG = LoggerFactory.getLogger(AccountPreferencesService.class);

    private final AccountPreferencesRepository accountPreferencesRepository;

    private final AccountPreferencesMapper accountPreferencesMapper;

    public AccountPreferencesService(
        AccountPreferencesRepository accountPreferencesRepository,
        AccountPreferencesMapper accountPreferencesMapper
    ) {
        this.accountPreferencesRepository = accountPreferencesRepository;
        this.accountPreferencesMapper = accountPreferencesMapper;
    }

    /**
     * Save a accountPreferences.
     *
     * @param accountPreferencesDTO the entity to save.
     * @return the persisted entity.
     */
    public AccountPreferencesDTO save(AccountPreferencesDTO accountPreferencesDTO) {
        LOG.debug("Request to save AccountPreferences : {}", accountPreferencesDTO);
        AccountPreferences accountPreferences = accountPreferencesMapper.toEntity(accountPreferencesDTO);
        accountPreferences = accountPreferencesRepository.save(accountPreferences);
        return accountPreferencesMapper.toDto(accountPreferences);
    }

    /**
     * Update a accountPreferences.
     *
     * @param accountPreferencesDTO the entity to save.
     * @return the persisted entity.
     */
    public AccountPreferencesDTO update(AccountPreferencesDTO accountPreferencesDTO) {
        LOG.debug("Request to update AccountPreferences : {}", accountPreferencesDTO);
        AccountPreferences accountPreferences = accountPreferencesMapper.toEntity(accountPreferencesDTO);
        accountPreferences = accountPreferencesRepository.save(accountPreferences);
        return accountPreferencesMapper.toDto(accountPreferences);
    }

    /**
     * Partially update a accountPreferences.
     *
     * @param accountPreferencesDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AccountPreferencesDTO> partialUpdate(AccountPreferencesDTO accountPreferencesDTO) {
        LOG.debug("Request to partially update AccountPreferences : {}", accountPreferencesDTO);

        return accountPreferencesRepository
            .findById(accountPreferencesDTO.getId())
            .map(existingAccountPreferences -> {
                accountPreferencesMapper.partialUpdate(existingAccountPreferences, accountPreferencesDTO);

                return existingAccountPreferences;
            })
            .map(accountPreferencesRepository::save)
            .map(accountPreferencesMapper::toDto);
    }

    /**
     * Get all the accountPreferences.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AccountPreferencesDTO> findAll() {
        LOG.debug("Request to get all AccountPreferences");
        return accountPreferencesRepository
            .findAll()
            .stream()
            .map(accountPreferencesMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one accountPreferences by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AccountPreferencesDTO> findOne(Long id) {
        LOG.debug("Request to get AccountPreferences : {}", id);
        return accountPreferencesRepository.findById(id).map(accountPreferencesMapper::toDto);
    }

    /**
     * Delete the accountPreferences by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete AccountPreferences : {}", id);
        accountPreferencesRepository.deleteById(id);
    }
}
