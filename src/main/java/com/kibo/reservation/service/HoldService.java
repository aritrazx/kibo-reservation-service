package com.kibo.reservation.service;

import com.kibo.reservation.dto.CreateHoldRequest;
import com.kibo.reservation.dto.HoldResponse;

/**
 * Service interface for hold operations.
 */
public interface HoldService {
    HoldResponse createHold(Long dropId, CreateHoldRequest request, long holdDurationSeconds);
    HoldResponse getHold(String holdId);
    HoldResponse confirm(String holdId);
    HoldResponse cancel(String holdId);
    int expireDueHolds();
}
