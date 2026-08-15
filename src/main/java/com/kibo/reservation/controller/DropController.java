package com.kibo.reservation.controller;

import com.kibo.reservation.dto.DropResponse;
import com.kibo.reservation.service.DropService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drops")
public class DropController {
    private final DropService dropService;

    public DropController(DropService dropService) {
        this.dropService = dropService;
    }

    @GetMapping("/{dropId}")
    public DropResponse get(@PathVariable Long dropId) {
        return dropService.get(dropId);
    }
}
