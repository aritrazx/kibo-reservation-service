package com.kibo.reservation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kibo.reservation.cache.AvailabilityCache;
import com.kibo.reservation.dto.CreateHoldRequest;
import com.kibo.reservation.entity.Drop;
import com.kibo.reservation.exception.InsufficientInventoryException;
import com.kibo.reservation.repository.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HoldServiceTest {
    @Mock DropRepository drops;
    @Mock HoldRepository holds;
    @Mock OutboxEventRepository outbox;
    @Mock AvailabilityCache cache;
    @InjectMocks HoldService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        service = new HoldService(drops, holds, outbox, cache, mapper);
    }

    @Test
    void createHold_rejectsWhenAtomicInventoryUpdateFails() {
        Drop drop = new Drop();
        drop.setId(1L);
        when(drops.findById(1L)).thenReturn(Optional.of(drop));
        when(drops.reserveUnits(1L, 2)).thenReturn(0);

        assertThrows(InsufficientInventoryException.class,
                () -> service.createHold(1L, new CreateHoldRequest("c1", 2), 300));

        verify(holds, never()).save(any());
        verify(outbox, never()).save(any());
    }

    @Test
    void createHold_reservesInventoryAndCreatesHold() {
        Drop drop = new Drop();
        drop.setId(1L);
        when(drops.findById(1L)).thenReturn(Optional.of(drop));
        when(drops.reserveUnits(1L, 2)).thenReturn(1);

        var response = service.createHold(1L, new CreateHoldRequest("c1", 2), 300);

        assertEquals("c1", response.customerId());
        assertEquals(2, response.quantity());
        verify(holds).save(any());
        verify(outbox).save(any());
        verify(cache).put(1L, -2);
    }
}
