package com.kibo.reservation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "holds")
@Getter @Setter @NoArgsConstructor
public class Hold {
    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "drop_id", nullable = false)
    private Drop drop;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HoldStatus status;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private long version;

    public static Hold create(Drop drop, String customerId, int quantity, Instant expiresAt) {
        Hold h = new Hold();
        h.id = UUID.randomUUID().toString();
        h.drop = drop;
        h.customerId = customerId;
        h.quantity = quantity;
        h.status = HoldStatus.ACTIVE;
        h.expiresAt = expiresAt;
        return h;
    }
}
