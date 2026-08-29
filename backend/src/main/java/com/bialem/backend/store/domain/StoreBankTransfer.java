package com.bialem.backend.store.domain;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.store.domain.enumeration.StorePaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "store_bank_transfer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StoreBankTransfer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 120)
    @Column(name = "reference_code", nullable = false, length = 120, unique = true)
    private String referenceCode;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 21, scale = 2)
    private BigDecimal amount;

    @Size(max = 8)
    @Column(name = "currency", length = 8)
    private String currency = "TRY";

    @Size(max = 64)
    @Column(name = "iban", length = 64)
    private String iban;

    @Size(max = 255)
    @Column(name = "account_holder", length = 255)
    private String accountHolder;

    @Size(max = 255)
    @Column(name = "bank_name", length = 255)
    private String bankName;

    @Column(name = "receipt_url", length = 2048)
    private String receiptUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StorePaymentStatus status = StorePaymentStatus.PENDING;

    @Size(max = 1000)
    @Column(name = "admin_note", length = 1000)
    private String adminNote;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true)
    @JsonIgnoreProperties(value = { "items", "statusHistory", "payment", "shipping" }, allowSetters = true)
    private StoreOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public StorePaymentStatus getStatus() {
        return status;
    }

    public void setStatus(StorePaymentStatus status) {
        this.status = status;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }

    public StoreOrder getOrder() {
        return order;
    }

    public void setOrder(StoreOrder order) {
        this.order = order;
    }

    public Profile getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Profile approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
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
}
