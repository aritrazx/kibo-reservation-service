package com.kibo.reservation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "drops")
@Getter @Setter @NoArgsConstructor
public class Drop {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "total_units", nullable = false)
    private int totalUnits;

    @Column(name = "available_units", nullable = false)
    private int availableUnits;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
