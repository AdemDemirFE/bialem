package com.bialem.backend.service;

import com.bialem.backend.domain.Role;
import com.bialem.backend.repository.RoleRepository;
import com.bialem.backend.service.dto.RoleDTO;
import com.bialem.backend.service.mapper.RoleMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.Role}.
 */
@Service
@Transactional
public class RoleService {

    private static final Logger LOG = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public RoleDTO save(RoleDTO roleDTO) {
        LOG.debug("Request to save Role : {}", roleDTO);
        Role role = roleMapper.toEntity(roleDTO);
        role = roleRepository.save(role);
        return roleMapper.toDto(role);
    }

    public RoleDTO update(RoleDTO roleDTO) {
        LOG.debug("Request to update Role : {}", roleDTO);
        Role role = roleMapper.toEntity(roleDTO);
        role = roleRepository.save(role);
        return roleMapper.toDto(role);
    }

    public Optional<RoleDTO> partialUpdate(RoleDTO roleDTO) {
        LOG.debug("Request to partially update Role : {}", roleDTO);

        return roleRepository
            .findById(roleDTO.getId())
            .map(existingRole -> {
                roleMapper.partialUpdate(existingRole, roleDTO);
                return existingRole;
            })
            .map(roleRepository::save)
            .map(roleMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<RoleDTO> findAll() {
        LOG.debug("Request to get all Roles");
        return roleRepository.findAll().stream().map(roleMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Transactional(readOnly = true)
    public Optional<RoleDTO> findOne(Long id) {
        LOG.debug("Request to get Role : {}", id);
        return roleRepository.findById(id).map(roleMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete Role : {}", id);
        roleRepository.deleteById(id);
    }
}
