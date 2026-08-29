package com.bialem.backend.store.web.rest;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.AppSupport;
import com.bialem.backend.store.service.StoreAddressService;
import com.bialem.backend.store.service.dto.StoreAddressDTO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store/addresses")
public class StoreAddressResource {

    private final StoreAddressService addressService;
    private final AppSupport appSupport;

    public StoreAddressResource(StoreAddressService addressService, AppSupport appSupport) {
        this.addressService = addressService;
        this.appSupport = appSupport;
    }

    @GetMapping
    public ResponseEntity<List<StoreAddressDTO>> getAddresses() {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(addressService.getAddresses(profile));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreAddressDTO> getAddress(@PathVariable Long id) {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(addressService.getAddress(profile, id));
    }

    @PostMapping
    public ResponseEntity<StoreAddressDTO> createAddress(@Valid @RequestBody StoreAddressDTO dto) {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(addressService.saveAddress(profile, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreAddressDTO> updateAddress(@PathVariable Long id, @Valid @RequestBody StoreAddressDTO dto) {
        Profile profile = appSupport.currentProfile();
        dto.setId(id);
        return ResponseEntity.ok(addressService.saveAddress(profile, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        Profile profile = appSupport.currentProfile();
        addressService.deleteAddress(profile, id);
        return ResponseEntity.noContent().build();
    }
}
