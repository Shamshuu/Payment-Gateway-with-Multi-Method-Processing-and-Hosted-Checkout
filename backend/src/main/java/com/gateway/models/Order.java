package com.gateway.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_merchant", columnList = "merchant_id")
})
public class Order {
    @Id
    @Column(length = 64)
    private String id; // Custom format: "order_" + 16 chars

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(nullable = false)
    private Long amount; // stored in paise

    @Column(length = 3)
    private String currency = "INR";

    private String receipt;

    @Column(columnDefinition = "TEXT") // Simplified JSON storage as text for compatibility
    private String notes;

    @Column(length = 20)
    private String status = "created";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}