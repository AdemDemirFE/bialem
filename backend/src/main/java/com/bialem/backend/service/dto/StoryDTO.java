package com.bialem.backend.service.dto;

import com.bialem.backend.domain.enumeration.StoryContentType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.Story} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StoryDTO implements Serializable {

    private Long id;

    @NotNull
    private StoryContentType contentType;

    @Size(max = 500)
    private String body;

    @Size(max = 2048)
    private String mediaUrl;

    @NotNull
    private Boolean isPublic;

    @NotNull
    private Boolean shareWithFollowers;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant expiresAt;

    private ProfileDTO author;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StoryContentType getContentType() {
        return contentType;
    }

    public void setContentType(StoryContentType contentType) {
        this.contentType = contentType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Boolean getShareWithFollowers() {
        return shareWithFollowers;
    }

    public void setShareWithFollowers(Boolean shareWithFollowers) {
        this.shareWithFollowers = shareWithFollowers;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public ProfileDTO getAuthor() {
        return author;
    }

    public void setAuthor(ProfileDTO author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StoryDTO)) {
            return false;
        }

        StoryDTO storyDTO = (StoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, storyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StoryDTO{" +
            "id=" + getId() +
            ", contentType='" + getContentType() + "'" +
            ", body='" + getBody() + "'" +
            ", mediaUrl='" + getMediaUrl() + "'" +
            ", isPublic='" + getIsPublic() + "'" +
            ", shareWithFollowers='" + getShareWithFollowers() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", author=" + getAuthor() +
            "}";
    }
}
