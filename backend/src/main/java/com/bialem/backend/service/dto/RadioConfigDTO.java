package com.bialem.backend.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.RadioConfig} entity.
 */
public class RadioConfigDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 200)
    private String radioName;

    @Size(max = 500)
    private String slogan;

    @Size(max = 2048)
    private String logo;

    @Size(max = 2048)
    private String cover;

    @Size(max = 2048)
    private String liveStreamUrl;

    @NotNull
    private Boolean isLive;

    @Size(max = 200)
    private String currentProgram;

    @Size(max = 200)
    private String currentTrack;

    @Size(max = 2048)
    private String websiteUrl;

    @Size(max = 2048)
    private String facebookUrl;

    @Size(max = 2048)
    private String twitterUrl;

    @Size(max = 2048)
    private String instagramUrl;

    @Size(max = 2048)
    private String youtubeUrl;

    @Size(max = 1000)
    private String metadataJson;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRadioName() {
        return radioName;
    }

    public void setRadioName(String radioName) {
        this.radioName = radioName;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getLiveStreamUrl() {
        return liveStreamUrl;
    }

    public void setLiveStreamUrl(String liveStreamUrl) {
        this.liveStreamUrl = liveStreamUrl;
    }

    public Boolean getIsLive() {
        return isLive;
    }

    public void setIsLive(Boolean isLive) {
        this.isLive = isLive;
    }

    public String getCurrentProgram() {
        return currentProgram;
    }

    public void setCurrentProgram(String currentProgram) {
        this.currentProgram = currentProgram;
    }

    public String getCurrentTrack() {
        return currentTrack;
    }

    public void setCurrentTrack(String currentTrack) {
        this.currentTrack = currentTrack;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getTwitterUrl() {
        return twitterUrl;
    }

    public void setTwitterUrl(String twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RadioConfigDTO)) return false;
        RadioConfigDTO that = (RadioConfigDTO) o;
        if (this.id == null) return false;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "RadioConfigDTO{" +
            "id=" + id +
            ", radioName='" + radioName + "'" +
            ", isLive=" + isLive +
            ", currentProgram='" + currentProgram + "'" +
            ", currentTrack='" + currentTrack + "'" +
            ", createdAt='" + createdAt + "'" +
            ", updatedAt='" + updatedAt + "'" +
            "}";
    }
}
