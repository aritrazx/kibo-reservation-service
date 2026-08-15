package com.kibo.reservation.dto;

import com.kibo.reservation.entity.Hold;
import java.time.Instant;

public record HoldResponse(
        String holdId, Long dropId, String customerId, int quantity,
        String status, Instant expiresAt
) {
    public static HoldResponse from(Hold h) {
        return new HoldResponse(h.getId(), h.getDrop().getId(), h.getCustomerId(),
                h.getQuantity(), h.getStatus().name(), h.getExpiresAt());
    }
}
