package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.RadioContentType;
import com.bialem.backend.domain.enumeration.RadioSourceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A RadioContent entity representing a piece of content for the Karasu Belediye Radio.
 */
@Entity
@Table(name = "radio_content")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RadioContent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private RadioContentType contentType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private RadioSourceType sourceType;

    @Size(max = 2048)
    @Column(name = "source_url", length = 2048)
    private String sourceUrl;

    @Size(max = 2048)
    @Column(name = "audio_file", length = 2048)
    private String audioFile;

    @Size(max = 2048)
    @Column(name = "thumbnail", length = 2048)
    private String thumbnail;

    @Size(max = 200)
    @Column(name = "artist", length = 200)
    private String artist;

    @Size(max = 200)
    @Column(name = "album", length = 200)
    private String album;

    @Column(name = "duration")
    private Integer duration;

    @Size(max = 100)
    @Column(name = "category", length = 100)
    private String category;

    @Size(max = 200)
    @Column(name = "program_name", length = 200)
    private String programName;

    @Size(max = 200)
    @Column(name = "presenter", length = 200)
    private String presenter;

    @Column(name = "publish_date")
    private Instant publishDate;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @NotNull
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "play_count")
    private Long playCount;

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

    public RadioContent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RadioContent title(String title) {
        this.setTitle(title);
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RadioContent description(String description) {
        this.setDescription(description);
        return this;
    }

    public RadioContentType getContentType() {
        return this.contentType;
    }

    public void setContentType(RadioContentType contentType) {
        this.contentType = contentType;
    }

    public RadioContent contentType(RadioContentType contentType) {
        this.setContentType(contentType);
        return this;
    }

    public RadioSourceType getSourceType() {
        return this.sourceType;
    }

    public void setSourceType(RadioSourceType sourceType) {
        this.sourceType = sourceType;
    }

    public RadioContent sourceType(RadioSourceType sourceType) {
        this.setSourceType(sourceType);
        return this;
    }

    public String getSourceUrl() {
        return this.sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public RadioContent sourceUrl(String sourceUrl) {
        this.setSourceUrl(sourceUrl);
        return this;
    }

    public String getAudioFile() {
        return this.audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    public RadioContent audioFile(String audioFile) {
        this.setAudioFile(audioFile);
        return this;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public RadioContent thumbnail(String thumbnail) {
        this.setThumbnail(thumbnail);
        return this;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public RadioContent artist(String artist) {
        this.setArtist(artist);
        return this;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public RadioContent album(String album) {
        this.setAlbum(album);
        return this;
    }

    public Integer getDuration() {
        return this.duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public RadioContent duration(Integer duration) {
        this.setDuration(duration);
        return this;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public RadioContent category(String category) {
        this.setCategory(category);
        return this;
    }

    public String getProgramName() {
        return this.programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public RadioContent programName(String programName) {
        this.setProgramName(programName);
        return this;
    }

    public String getPresenter() {
        return this.presenter;
    }

    public void setPresenter(String presenter) {
        this.presenter = presenter;
    }

    public RadioContent presenter(String presenter) {
        this.setPresenter(presenter);
        return this;
    }

    public Instant getPublishDate() {
        return this.publishDate;
    }

    public void setPublishDate(Instant publishDate) {
        this.publishDate = publishDate;
    }

    public RadioContent publishDate(Instant publishDate) {
        this.setPublishDate(publishDate);
        return this;
    }

    public Instant getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public RadioContent startDate(Instant startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public RadioContent endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public RadioContent isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public Boolean getIsFeatured() {
        return this.isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public RadioContent isFeatured(Boolean isFeatured) {
        this.setIsFeatured(isFeatured);
        return this;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public RadioContent sortOrder(Integer sortOrder) {
        this.setSortOrder(sortOrder);
        return this;
    }

    public Long getPlayCount() {
        return this.playCount;
    }

    public void setPlayCount(Long playCount) {
        this.playCount = playCount;
    }

    public RadioContent playCount(Long playCount) {
        this.setPlayCount(playCount);
        return this;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public RadioContent createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public RadioContent updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RadioContent)) {
            return false;
        }
        return getId() != null && getId().equals(((RadioContent) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "RadioContent{" +
            "id=" +
            getId() +
            ", title='" +
            getTitle() +
            "'" +
            ", contentType='" +
            getContentType() +
            "'" +
            ", sourceType='" +
            getSourceType() +
            "'" +
            ", isActive='" +
            getIsActive() +
            "'" +
            ", isFeatured='" +
            getIsFeatured() +
            "'" +
            ", playCount=" +
            getPlayCount() +
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
