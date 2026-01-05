package com.gateway.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_order", columnList = "order_id"),
    @Index(name = "idx_payment_status", columnList = "status")
})
public class Payment {
    @Id
    @Column(length = 64)
    private String id; // Custom format: "pay_" + 16 chars

    @Column(name = "order_id", nullable = false, length = 64)
    private String orderId;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(nullable = false)
    private Long amount;

    @Column(length = 3)
    private String currency = "INR";

    @Column(nullable = false, length = 20)
    private String method; // "upi" or "card"

    @Column(length = 20)
    private String status; // "processing", "success", "failed"

    private String vpa;

    @Column(name = "card_network", length = 20)
    private String cardNetwork;

    @Column(name = "card_last4", length = 4)
    private String cardLast4;

    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Column(name = "error_description", columnDefinition = "TEXT")
    private String errorDescription;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}