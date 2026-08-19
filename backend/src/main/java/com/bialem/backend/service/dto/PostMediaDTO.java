package com.bialem.backend.service.dto;

import com.bialem.backend.domain.enumeration.MediaType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.PostMedia} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PostMediaDTO implements Serializable {

    private Long id;

    @NotNull
    private MediaType mediaType;

    @NotNull
    @Size(max = 512)
    private String storagePath;

    @NotNull
    private Integer sortOrder;

    @NotNull
    private Instant createdAt;

    private PostDTO post;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public PostDTO getPost() {
        return post;
    }

    public void setPost(PostDTO post) {
        this.post = post;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PostMediaDTO)) {
            return false;
        }

        PostMediaDTO postMediaDTO = (PostMediaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, postMediaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PostMediaDTO{" +
            "id=" + getId() +
            ", mediaType='" + getMediaType() + "'" +
            ", storagePath='" + getStoragePath() + "'" +
            ", sortOrder=" + getSortOrder() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", post=" + getPost() +
            "}";
    }
}
