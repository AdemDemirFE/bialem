package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.StoryElementType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A StoryElement.
 */
@Entity
@Table(name = "story_element")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StoryElement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "element_type", nullable = false)
    private StoryElementType elementType;

    @Size(max = 2000)
    @Column(name = "content", length = 2000)
    private String content;

    @Column(name = "position_x")
    private Double positionX;

    @Column(name = "position_y")
    private Double positionY;

    @Column(name = "scale")
    private Double scale;

    @Column(name = "rotation")
    private Double rotation;

    @Size(max = 50)
    @Column(name = "color", length = 50)
    private String color;

    @Size(max = 50)
    @Column(name = "background_color", length = 50)
    private String backgroundColor;

    @Column(name = "font_size")
    private Integer fontSize;

    @Column(name = "width")
    private Double width;

    @Column(name = "height")
    private Double height;

    @Size(max = 4000)
    @JdbcTypeCode(SqlTypes.LONG32VARCHAR)
    @Column(name = "metadata_json", length = 4000)
    private String metadataJson;

    @NotNull
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "author", "views", "communityTargets", "storyHashtags", "storyElements", "storyGroup", "event" }, allowSetters = true)
    private Story story;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StoryElement id(Long id) {
        this.setId(id);
        return this;
    }

    public StoryElementType getElementType() {
        return elementType;
    }

    public void setElementType(StoryElementType elementType) {
        this.elementType = elementType;
    }

    public StoryElement elementType(StoryElementType elementType) {
        this.setElementType(elementType);
        return this;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public StoryElement content(String content) {
        this.setContent(content);
        return this;
    }

    public Double getPositionX() {
        return positionX;
    }

    public void setPositionX(Double positionX) {
        this.positionX = positionX;
    }

    public StoryElement positionX(Double positionX) {
        this.setPositionX(positionX);
        return this;
    }

    public Double getPositionY() {
        return positionY;
    }

    public void setPositionY(Double positionY) {
        this.positionY = positionY;
    }

    public StoryElement positionY(Double positionY) {
        this.setPositionY(positionY);
        return this;
    }

    public Double getScale() {
        return scale;
    }

    public void setScale(Double scale) {
        this.scale = scale;
    }

    public StoryElement scale(Double scale) {
        this.setScale(scale);
        return this;
    }

    public Double getRotation() {
        return rotation;
    }

    public void setRotation(Double rotation) {
        this.rotation = rotation;
    }

    public StoryElement rotation(Double rotation) {
        this.setRotation(rotation);
        return this;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public StoryElement color(String color) {
        this.setColor(color);
        return this;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public StoryElement backgroundColor(String backgroundColor) {
        this.setBackgroundColor(backgroundColor);
        return this;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public StoryElement fontSize(Integer fontSize) {
        this.setFontSize(fontSize);
        return this;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public StoryElement width(Double width) {
        this.setWidth(width);
        return this;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public StoryElement height(Double height) {
        this.setHeight(height);
        return this;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }

    public StoryElement metadataJson(String metadataJson) {
        this.setMetadataJson(metadataJson);
        return this;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public StoryElement sortOrder(Integer sortOrder) {
        this.setSortOrder(sortOrder);
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public StoryElement createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public StoryElement story(Story story) {
        this.setStory(story);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StoryElement)) {
            return false;
        }
        return getId() != null && getId().equals(((StoryElement) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "StoryElement{" +
            "id=" + getId() +
            ", elementType='" + getElementType() + "'" +
            ", sortOrder=" + getSortOrder() +
            "}";
    }
}
