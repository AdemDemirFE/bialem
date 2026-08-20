package com.bialem.backend.web.rest;

import com.bialem.backend.service.PushDeviceTokenService;
import com.bialem.backend.service.dto.PushDeviceTokenDTO;
import com.bialem.backend.service.dto.PushDeviceTokenRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/push-device-tokens")
public class PushDeviceTokenResource {

    private static final Logger LOG = LoggerFactory.getLogger(PushDeviceTokenResource.class);

    private final PushDeviceTokenService pushDeviceTokenService;

    public PushDeviceTokenResource(PushDeviceTokenService pushDeviceTokenService) {
        this.pushDeviceTokenService = pushDeviceTokenService;
    }

    @PostMapping("")
    public ResponseEntity<PushDeviceTokenDTO> register(@Valid @RequestBody PushDeviceTokenRequest request) throws URISyntaxException {
        LOG.debug("REST request to register push device token");
        PushDeviceTokenDTO result = pushDeviceTokenService.registerCurrentUser(request);
        return ResponseEntity.created(new URI("/api/push-device-tokens/" + result.getId())).body(result);
    }
}
