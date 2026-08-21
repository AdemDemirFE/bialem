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

    @Size(max = 255)
    private String firebaseInstallationId;

    @Size(max = 255)
    private String deviceUuid;

    @Size(max = 80)
    private String appVersion;

    private Boolean notificationsEnabled;

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

    public String getFirebaseInstallationId() {
        return firebaseInstallationId;
    }

    public void setFirebaseInstallationId(String firebaseInstallationId) {
        this.firebaseInstallationId = firebaseInstallationId;
    }

    public String getDeviceUuid() {
        return deviceUuid;
    }

    public void setDeviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
}
