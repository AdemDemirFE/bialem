package com.bialem.backend.store.service.dto;

import com.bialem.backend.store.domain.enumeration.StoreOrderStatus;
import java.io.Serializable;
import java.time.Instant;

public class StoreOrderStatusHistoryDTO implements Serializable {

    private Long id;
    private StoreOrderStatus oldStatus;
    private StoreOrderStatus newStatus;
    private String changedBy;
    private String note;
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
