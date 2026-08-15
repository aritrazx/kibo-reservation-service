package com.kibo.reservation.controller;

import com.kibo.reservation.dto.DropResponse;
import com.kibo.reservation.service.DropService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drops")
@RequiredArgsConstructor
public class DropController {
    private final DropService dropService;

    @GetMapping("/{dropId}")
    public DropResponse get(@PathVariable Long dropId) {
        return dropService.get(dropId);
    }
}
