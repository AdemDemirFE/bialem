package com.bialem.backend.store.domain;

import com.bialem.backend.store.domain.enumeration.StoreOrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "store_order_status_history")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StoreOrderStatusHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnoreProperties(value = { "items", "statusHistory", "payment", "shipping" }, allowSetters = true)
    private StoreOrder order;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status")
    private StoreOrderStatus oldStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private StoreOrderStatus newStatus;

    @Size(max = 80)
    @Column(name = "changed_by", length = 80)
    private String changedBy;

    @Size(max = 2000)
    @Column(name = "note", length = 2000)
    private String note;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StoreOrder getOrder() {
        return order;
    }

    public void setOrder(StoreOrder order) {
        this.order = order;
    }

    public StoreOrderStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(StoreOrderStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public StoreOrderStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(StoreOrderStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
