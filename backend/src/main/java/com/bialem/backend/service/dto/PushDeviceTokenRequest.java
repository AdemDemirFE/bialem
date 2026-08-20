package com.bialem.backend.service.dto;

import com.bialem.backend.domain.enumeration.PushPlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public class PushDeviceTokenRequest implements Serializable {

    @NotBlank
    @Size(max = 512)
    private String token;

    private PushPlatform platform;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public PushPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(PushPlatform platform) {
        this.platform = platform;
    }
}
