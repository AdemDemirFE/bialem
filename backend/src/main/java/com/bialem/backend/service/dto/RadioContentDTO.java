package com.bialem.backend.service.dto;

import com.bialem.backend.domain.enumeration.RadioContentType;
import com.bialem.backend.domain.enumeration.RadioSourceType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.RadioContent} entity.
 */
public class RadioContentDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String title;

    private String description;

    @NotNull
    private RadioContentType contentType;

    @NotNull
    private RadioSourceType sourceType;

    @Size(max = 2048)
    private String sourceUrl;

    @Size(max = 2048)
    private String audioFile;

    @Size(max = 2048)
    private String thumbnail;

    @Size(max = 200)
    private String artist;

    @Size(max = 200)
    private String album;

    private Integer duration;

    @Size(max = 100)
    private String category;

    @Size(max = 200)
    private String programName;

    @Size(max = 200)
    private String presenter;

    private Instant publishDate;

    private Instant startDate;

    private Instant endDate;

    @NotNull
    private Boolean isActive;

    @NotNull
    private Boolean isFeatured;

    private Integer sortOrder;

    private Long playCount;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RadioContentType getContentType() {
        return contentType;
    }

    public void setContentType(RadioContentType contentType) {
        this.contentType = contentType;
    }

    public RadioSourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(RadioSourceType sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getPresenter() {
        return presenter;
    }

    public void setPresenter(String presenter) {
        this.presenter = presenter;
    }

    public Instant getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Instant publishDate) {
        this.publishDate = publishDate;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Long getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Long playCount) {
        this.playCount = playCount;
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
        if (!(o instanceof RadioContentDTO)) return false;
        RadioContentDTO that = (RadioContentDTO) o;
        if (this.id == null) return false;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "RadioContentDTO{" +
            "id=" + id +
            ", title='" + title + "'" +
            ", contentType='" + contentType + "'" +
            ", sourceType='" + sourceType + "'" +
            ", isActive=" + isActive +
            ", isFeatured=" + isFeatured +
            ", playCount=" + playCount +
            ", createdAt='" + createdAt + "'" +
            ", updatedAt='" + updatedAt + "'" +
            "}";
    }
}
