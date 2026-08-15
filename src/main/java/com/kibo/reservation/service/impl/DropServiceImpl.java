package com.kibo.reservation.service.impl;

import com.kibo.reservation.cache.AvailabilityCache;
import com.kibo.reservation.dto.DropResponse;
import com.kibo.reservation.exception.NotFoundException;
import com.kibo.reservation.repository.DropRepository;
import com.kibo.reservation.service.DropService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class DropServiceImpl implements DropService {
    private final DropRepository repository;
    private final AvailabilityCache cache;

    public DropServiceImpl(DropRepository repository, AvailabilityCache cache) {
        this.repository = repository;
        this.cache = cache;
    }

    @Transactional(readOnly = true)
    public DropResponse get(Long id) {
        return repository.findById(id)
                .map(drop -> {
                    int available = cache.get(id).orElse(drop.getAvailableUnits());
                    cache.put(id, available);
                    return new DropResponse(
                            drop.getId(),
                            drop.getName(),
                            drop.getTotalUnits(),
                            available,
                            drop.getStartTime()
                    );
                })
                .orElseThrow(() -> new NotFoundException("Drop not found: " + id));
    }

}
