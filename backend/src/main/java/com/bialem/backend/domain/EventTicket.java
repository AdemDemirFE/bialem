package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.EventTicketStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A EventTicket.
 */
@Entity
@Table(name = "event_ticket")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventTicket implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, length = 160)
    private String name;

    @Column(name = "description", length = 2000)
    private String description;

    @NotNull
    @Column(name = "price", nullable = false, precision = 21, scale = 2)
    private BigDecimal price;

    @NotNull
    @Column(name = "currency", nullable = false, length = 8)
    private String currency;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "sold_quantity")
    private Integer soldQuantity = 0;

    @Column(name = "sale_start_date")
    private Instant saleStartDate;

    @Column(name = "sale_end_date")
    private Instant saleEndDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventTicketStatus status = EventTicketStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "community", "category", "createdBy", "cancelledBy", "participants", "messages", "ratings", "posts" }, allowSetters = true)
    private Event event;

    public Long getId() {
        return this.id;
    }

    public EventTicket id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public EventTicket name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public EventTicket description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public EventTicket price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return this.currency;
    }

    public EventTicket currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public EventTicket quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getSoldQuantity() {
        return this.soldQuantity;
    }

    public EventTicket soldQuantity(Integer soldQuantity) {
        this.setSoldQuantity(soldQuantity);
        return this;
    }

    public void setSoldQuantity(Integer soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public Instant getSaleStartDate() {
        return this.saleStartDate;
    }

    public EventTicket saleStartDate(Instant saleStartDate) {
        this.setSaleStartDate(saleStartDate);
        return this;
    }

    public void setSaleStartDate(Instant saleStartDate) {
        this.saleStartDate = saleStartDate;
    }

    public Instant getSaleEndDate() {
        return this.saleEndDate;
    }

    public EventTicket saleEndDate(Instant saleEndDate) {
        this.setSaleEndDate(saleEndDate);
        return this;
    }

    public void setSaleEndDate(Instant saleEndDate) {
        this.saleEndDate = saleEndDate;
    }

    public EventTicketStatus getStatus() {
        return this.status;
    }

    public EventTicket status(EventTicketStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(EventTicketStatus status) {
        this.status = status;
    }

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public EventTicket event(Event event) {
        this.setEvent(event);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventTicket)) {
            return false;
        }
        return getId() != null && getId().equals(((EventTicket) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "EventTicket{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", price=" + getPrice() +
            ", currency='" + getCurrency() + "'" +
            "}";
    }
}
