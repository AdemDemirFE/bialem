package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.TicketStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Ticket.
 */
@Entity
@Table(
    name = "ticket",
    uniqueConstraints = { @UniqueConstraint(name = "ux_ticket__ticket_code", columnNames = { "ticket_code" }) }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "ticket_code", nullable = false, unique = true, length = 120)
    private String ticketCode;

    @Column(name = "qr_code", length = 2048)
    private String qrCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TicketStatus status = TicketStatus.ACTIVE;

    @Column(name = "used_at")
    private Instant usedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "status", "createdAt", "paidAt" }, allowSetters = true)
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "community", "category", "createdBy", "cancelledBy", "participants", "messages", "ratings", "posts" }, allowSetters = true)
    private Event event;

    public Long getId() {
        return this.id;
    }

    public Ticket id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketCode() {
        return this.ticketCode;
    }

    public Ticket ticketCode(String ticketCode) {
        this.setTicketCode(ticketCode);
        return this;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public String getQrCode() {
        return this.qrCode;
    }

    public Ticket qrCode(String qrCode) {
        this.setQrCode(qrCode);
        return this;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public TicketStatus getStatus() {
        return this.status;
    }

    public Ticket status(TicketStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public Instant getUsedAt() {
        return this.usedAt;
    }

    public Ticket usedAt(Instant usedAt) {
        this.setUsedAt(usedAt);
        return this;
    }

    public void setUsedAt(Instant usedAt) {
        this.usedAt = usedAt;
    }

    public OrderItem getOrderItem() {
        return this.orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public Ticket orderItem(OrderItem orderItem) {
        this.setOrderItem(orderItem);
        return this;
    }

    public Profile getUser() {
        return this.user;
    }

    public void setUser(Profile profile) {
        this.user = profile;
    }

    public Ticket user(Profile profile) {
        this.setUser(profile);
        return this;
    }

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Ticket event(Event event) {
        this.setEvent(event);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ticket)) {
            return false;
        }
        return getId() != null && getId().equals(((Ticket) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Ticket{" +
            "id=" + getId() +
            ", ticketCode='" + getTicketCode() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
