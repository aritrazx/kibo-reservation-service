package com.kibo.reservation.dto;

import jakarta.validation.constraints.*;

public record CreateHoldRequest(
        @NotBlank String customerId,
        @Min(1) @Max(100000) int quantity
) {}
