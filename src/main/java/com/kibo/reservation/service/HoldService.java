package com.kibo.reservation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kibo.reservation.cache.AvailabilityCache;
import com.kibo.reservation.dto.*;
import com.kibo.reservation.entity.*;
import com.kibo.reservation.exception.*;
import com.kibo.reservation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HoldService {
    private final DropRepository dropRepository;
    private final HoldRepository holdRepository;
    private final OutboxEventRepository outboxRepository;
    private final AvailabilityCache availabilityCache;
    private final ObjectMapper objectMapper;

    @Transactional
    public HoldResponse createHold(Long dropId, CreateHoldRequest request, long holdDurationSeconds) {
        Drop drop = dropRepository.findById(dropId)
                .orElseThrow(() -> new NotFoundException("Drop not found: " + dropId));

        int updated = dropRepository.reserveUnits(dropId, request.quantity());
        if (updated == 0) {
            throw new InsufficientInventoryException("Insufficient inventory for drop " + dropId);
        }

        int nextAvailable = drop.getAvailableUnits() - request.quantity();
        availabilityCache.put(dropId, nextAvailable);

        Hold hold = Hold.create(drop, request.customerId(), request.quantity(),
                Instant.now().plusSeconds(holdDurationSeconds));
        holdRepository.save(hold);

        addOutbox("HOLD_CREATED", hold);
        return HoldResponse.from(hold);
    }

    @Transactional(readOnly = true)
    public HoldResponse getHold(String holdId) {
        return HoldResponse.from(holdRepository.findByIdWithDrop(holdId)
                .orElseThrow(() -> new NotFoundException("Hold not found: " + holdId)));
    }

    @Transactional
    public HoldResponse confirm(String holdId) {
        Hold hold = find(holdId);
        Instant now = Instant.now();

        if (hold.getStatus() != HoldStatus.ACTIVE) {
            throw new ConflictException("Hold is not active");
        }
        if (!now.isBefore(hold.getExpiresAt())) {
            expireInternal(hold, now);
            throw new ConflictException("Hold has expired");
        }

        int changed = holdRepository.transition(holdId, HoldStatus.ACTIVE, HoldStatus.CONFIRMED);
        if (changed == 0) throw new ConflictException("Hold is no longer active");

        hold.setStatus(HoldStatus.CONFIRMED);
        addOutbox("HOLD_CONFIRMED", hold);
        return HoldResponse.from(hold);
    }

    @Transactional
    public HoldResponse cancel(String holdId) {
        Hold hold = find(holdId);
        if (hold.getStatus() != HoldStatus.ACTIVE) {
            throw new ConflictException("Only an active hold can be cancelled");
        }

        int changed = holdRepository.transition(holdId, HoldStatus.ACTIVE, HoldStatus.CANCELLED);
        if (changed == 0) throw new ConflictException("Hold is no longer active");

        dropRepository.releaseUnits(hold.getDrop().getId(), hold.getQuantity());
        availabilityCache.put(hold.getDrop().getId(), hold.getDrop().getAvailableUnits() + hold.getQuantity());
        hold.setStatus(HoldStatus.CANCELLED);
        addOutbox("HOLD_CANCELLED", hold);
        return HoldResponse.from(hold);
    }

    @Transactional
    public int expireDueHolds() {
        int count = 0;
        Instant now = Instant.now();
        for (Hold hold : holdRepository.findTop100ByStatusAndExpiresAtLessThanEqualOrderByExpiresAtAsc(
                HoldStatus.ACTIVE, now)) {
            count += expireInternal(hold, now) ? 1 : 0;
        }
        return count;
    }

    private boolean expireInternal(Hold hold, Instant now) {
        int changed = holdRepository.expireIfActive(
                hold.getId(), HoldStatus.ACTIVE, HoldStatus.EXPIRED, now);
        if (changed == 0) return false;

        dropRepository.releaseUnits(hold.getDrop().getId(), hold.getQuantity());
        availabilityCache.put(hold.getDrop().getId(), hold.getDrop().getAvailableUnits() + hold.getQuantity());
        hold.setStatus(HoldStatus.EXPIRED);
        addOutbox("HOLD_EXPIRED", hold);
        return true;
    }

    private Hold find(String holdId) {
        return holdRepository.findByIdWithDrop(holdId)
                .orElseThrow(() -> new NotFoundException("Hold not found: " + holdId));
    }

    private void addOutbox(String type, Hold hold) {
        try {
            OutboxEvent e = new OutboxEvent();
            e.setId(UUID.randomUUID().toString());
            e.setAggregateType("HOLD");
            e.setAggregateId(hold.getId());
            e.setEventType(type);
            e.setPayload(objectMapper.writeValueAsString(Map.of(
                    "eventId", e.getId(),
                    "eventType", type,
                    "holdId", hold.getId(),
                    "dropId", hold.getDrop().getId(),
                    "customerId", hold.getCustomerId(),
                    "quantity", hold.getQuantity(),
                    "occurredAt", Instant.now()
            )));
            e.setCreatedAt(Instant.now());
            outboxRepository.save(e);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to create outbox event", ex);
        }
    }
}
