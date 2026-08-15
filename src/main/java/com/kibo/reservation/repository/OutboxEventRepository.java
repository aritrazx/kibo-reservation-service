package com.kibo.reservation.repository;

import com.kibo.reservation.entity.OutboxEvent;
import org.springframework.data.jpa.repository.*;
import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {
    List<OutboxEvent> findTop100ByPublishedAtIsNullOrderByCreatedAtAsc();
}
