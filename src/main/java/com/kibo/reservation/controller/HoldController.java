package com.kibo.reservation.controller;

import com.kibo.reservation.dto.*;
import com.kibo.reservation.service.HoldService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class HoldController {
    private final HoldService holdService;
    private final HoldProperties properties;

    public HoldController(HoldService holdService, HoldProperties properties) {
        this.holdService = holdService;
        this.properties = properties;
    }

    @PostMapping("/drops/{dropId}/holds")
    public ResponseEntity<HoldResponse> create(
            @PathVariable Long dropId, @Valid @RequestBody CreateHoldRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(holdService.createHold(dropId, request, properties.durationSeconds()));
    }

    @GetMapping("/holds/{holdId}")
    public HoldResponse get(@PathVariable String holdId) {
        return holdService.getHold(holdId);
    }

    @PostMapping("/holds/{holdId}/confirm")
    public HoldResponse confirm(@PathVariable String holdId) {
        return holdService.confirm(holdId);
    }

    @PostMapping("/holds/{holdId}/cancel")
    public HoldResponse cancel(@PathVariable String holdId) {
        return holdService.cancel(holdId);
    }
}
