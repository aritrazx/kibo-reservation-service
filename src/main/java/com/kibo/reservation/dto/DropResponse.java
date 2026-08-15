package com.kibo.reservation.dto;

import com.kibo.reservation.entity.Drop;
import java.time.Instant;

public record DropResponse(
        Long id, String name, int totalUnits, int availableUnits, Instant startTime
) {
    public static DropResponse from(Drop d) {
        return new DropResponse(d.getId(), d.getName(), d.getTotalUnits(),
                d.getAvailableUnits(), d.getStartTime());
    }
}
