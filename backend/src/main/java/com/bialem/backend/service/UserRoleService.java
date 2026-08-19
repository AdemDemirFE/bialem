package com.bialem.backend.service;

import com.bialem.backend.domain.UserRole;
import com.bialem.backend.repository.UserRoleRepository;
import com.bialem.backend.service.dto.UserRoleDTO;
import com.bialem.backend.service.mapper.UserRoleMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.UserRole}.
 */
@Service
@Transactional
public class UserRoleService {

    private static final Logger LOG = LoggerFactory.getLogger(UserRoleService.class);

    private final UserRoleRepository userRoleRepository;

    private final UserRoleMapper userRoleMapper;

    public UserRoleService(UserRoleRepository userRoleRepository, UserRoleMapper userRoleMapper) {
        this.userRoleRepository = userRoleRepository;
        this.userRoleMapper = userRoleMapper;
    }

    public UserRoleDTO save(UserRoleDTO userRoleDTO) {
        LOG.debug("Request to save UserRole : {}", userRoleDTO);
        UserRole userRole = userRoleMapper.toEntity(userRoleDTO);
        userRole = userRoleRepository.save(userRole);
        return userRoleMapper.toDto(userRole);
    }

    public UserRoleDTO update(UserRoleDTO userRoleDTO) {
        LOG.debug("Request to update UserRole : {}", userRoleDTO);
        UserRole userRole = userRoleMapper.toEntity(userRoleDTO);
        userRole = userRoleRepository.save(userRole);
        return userRoleMapper.toDto(userRole);
    }

    public Optional<UserRoleDTO> partialUpdate(UserRoleDTO userRoleDTO) {
        LOG.debug("Request to partially update UserRole : {}", userRoleDTO);

        return userRoleRepository
            .findById(userRoleDTO.getId())
            .map(existingUserRole -> {
                userRoleMapper.partialUpdate(existingUserRole, userRoleDTO);
                return existingUserRole;
            })
            .map(userRoleRepository::save)
            .map(userRoleMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<UserRoleDTO> findAll() {
        LOG.debug("Request to get all UserRoles");
        return userRoleRepository.findAll().stream().map(userRoleMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Transactional(readOnly = true)
    public Optional<UserRoleDTO> findOne(Long id) {
        LOG.debug("Request to get UserRole : {}", id);
        return userRoleRepository.findById(id).map(userRoleMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete UserRole : {}", id);
        userRoleRepository.deleteById(id);
    }
}
