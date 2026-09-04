package com.bialem.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RadioConfig entity representing the station configuration for Karasu Belediye Radio.
 */
@Entity
@Table(name = "radio_config")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RadioConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 200)
    @Column(name = "radio_name", length = 200, nullable = false)
    private String radioName;

    @Size(max = 500)
    @Column(name = "slogan", length = 500)
    private String slogan;

    @Size(max = 2048)
    @Column(name = "logo", length = 2048)
    private String logo;

    @Size(max = 2048)
    @Column(name = "cover", length = 2048)
    private String cover;

    @Size(max = 2048)
    @Column(name = "live_stream_url", length = 2048)
    private String liveStreamUrl;

    @NotNull
    @Column(name = "is_live", nullable = false)
    private Boolean isLive;

    @Size(max = 200)
    @Column(name = "current_program", length = 200)
    private String currentProgram;

    @Size(max = 200)
    @Column(name = "current_track", length = 200)
    private String currentTrack;

    @Size(max = 2048)
    @Column(name = "website_url", length = 2048)
    private String websiteUrl;

    @Size(max = 2048)
    @Column(name = "facebook_url", length = 2048)
    private String facebookUrl;

    @Size(max = 2048)
    @Column(name = "twitter_url", length = 2048)
    private String twitterUrl;

    @Size(max = 2048)
    @Column(name = "instagram_url", length = 2048)
    private String instagramUrl;

    @Size(max = 2048)
    @Column(name = "youtube_url", length = 2048)
    private String youtubeUrl;

    @Size(max = 1000)
    @Column(name = "metadata_json", length = 1000)
    private String metadataJson;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RadioConfig id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRadioName() {
        return this.radioName;
    }

    public void setRadioName(String radioName) {
        this.radioName = radioName;
    }

    public RadioConfig radioName(String radioName) {
        this.setRadioName(radioName);
        return this;
    }

    public String getSlogan() {
        return this.slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public RadioConfig slogan(String slogan) {
        this.setSlogan(slogan);
        return this;
    }

    public String getLogo() {
        return this.logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public RadioConfig logo(String logo) {
        this.setLogo(logo);
        return this;
    }

    public String getCover() {
        return this.cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public RadioConfig cover(String cover) {
        this.setCover(cover);
        return this;
    }

    public String getLiveStreamUrl() {
        return this.liveStreamUrl;
    }

    public void setLiveStreamUrl(String liveStreamUrl) {
        this.liveStreamUrl = liveStreamUrl;
    }

    public RadioConfig liveStreamUrl(String liveStreamUrl) {
        this.setLiveStreamUrl(liveStreamUrl);
        return this;
    }

    public Boolean getIsLive() {
        return this.isLive;
    }

    public void setIsLive(Boolean isLive) {
        this.isLive = isLive;
    }

    public RadioConfig isLive(Boolean isLive) {
        this.setIsLive(isLive);
        return this;
    }

    public String getCurrentProgram() {
        return this.currentProgram;
    }

    public void setCurrentProgram(String currentProgram) {
        this.currentProgram = currentProgram;
    }

    public RadioConfig currentProgram(String currentProgram) {
        this.setCurrentProgram(currentProgram);
        return this;
    }

    public String getCurrentTrack() {
        return this.currentTrack;
    }

    public void setCurrentTrack(String currentTrack) {
        this.currentTrack = currentTrack;
    }

    public RadioConfig currentTrack(String currentTrack) {
        this.setCurrentTrack(currentTrack);
        return this;
    }

    public String getWebsiteUrl() {
        return this.websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public RadioConfig websiteUrl(String websiteUrl) {
        this.setWebsiteUrl(websiteUrl);
        return this;
    }

    public String getFacebookUrl() {
        return this.facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public RadioConfig facebookUrl(String facebookUrl) {
        this.setFacebookUrl(facebookUrl);
        return this;
    }

    public String getTwitterUrl() {
        return this.twitterUrl;
    }

    public void setTwitterUrl(String twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    public RadioConfig twitterUrl(String twitterUrl) {
        this.setTwitterUrl(twitterUrl);
        return this;
    }

    public String getInstagramUrl() {
        return this.instagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public RadioConfig instagramUrl(String instagramUrl) {
        this.setInstagramUrl(instagramUrl);
        return this;
    }

    public String getYoutubeUrl() {
        return this.youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public RadioConfig youtubeUrl(String youtubeUrl) {
        this.setYoutubeUrl(youtubeUrl);
        return this;
    }

    public String getMetadataJson() {
        return this.metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }

    public RadioConfig metadataJson(String metadataJson) {
        this.setMetadataJson(metadataJson);
        return this;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public RadioConfig createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public RadioConfig updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RadioConfig)) {
            return false;
        }
        return getId() != null && getId().equals(((RadioConfig) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "RadioConfig{" +
            "id=" +
            getId() +
            ", radioName='" +
            getRadioName() +
            "'" +
            ", slogan='" +
            getSlogan() +
            "'" +
            ", isLive=" +
            getIsLive() +
            ", currentProgram='" +
            getCurrentProgram() +
            "'" +
            ", currentTrack='" +
            getCurrentTrack() +
            "'" +
            ", createdAt='" +
            getCreatedAt() +
            "'" +
            ", updatedAt='" +
            getUpdatedAt() +
            "'" +
            "}"
        );
    }
}
