package com.bialem.backend.store.service;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.store.domain.StoreAddress;
import com.bialem.backend.store.repository.StoreAddressRepository;
import com.bialem.backend.store.service.dto.StoreAddressDTO;
import com.bialem.backend.store.service.mapper.StoreMapper;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class StoreAddressService {

    private final StoreAddressRepository addressRepository;
    private final StoreMapper mapper;

    public StoreAddressService(StoreAddressRepository addressRepository, StoreMapper mapper) {
        this.addressRepository = addressRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<StoreAddressDTO> getAddresses(Profile user) {
        return addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(user.getId()).stream().map(mapper::toAddressDTO).toList();
    }

    @Transactional(readOnly = true)
    public StoreAddressDTO getAddress(Profile user, Long id) {
        return addressRepository
            .findByIdAndUserId(id, user.getId())
            .map(mapper::toAddressDTO)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Adres bulunamadı"));
    }

    public StoreAddressDTO saveAddress(Profile user, StoreAddressDTO dto) {
        StoreAddress address;
        if (dto.getId() != null) {
            address = addressRepository
                .findByIdAndUserId(dto.getId(), user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Adres bulunamadı"));
            address.setUpdatedAt(Instant.now());
        } else {
            address = new StoreAddress();
            address.setUser(user);
            address.setCreatedAt(Instant.now());
        }
        mapper.updateAddressFromDTO(address, dto);
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            addressRepository
                .findByUserIdOrderByIsDefaultDescCreatedAtDesc(user.getId())
                .stream()
                .filter(a -> !a.getId().equals(address.getId()))
                .forEach(a -> {
                    a.setIsDefault(false);
                    a.setUpdatedAt(Instant.now());
                });
        }
        return mapper.toAddressDTO(addressRepository.save(address));
    }

    public void deleteAddress(Profile user, Long id) {
        StoreAddress address = addressRepository
            .findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Adres bulunamadı"));
        addressRepository.delete(address);
    }
}
