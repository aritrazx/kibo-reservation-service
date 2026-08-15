package com.kibo.reservation.repository;

import com.kibo.reservation.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.*;

public interface HoldRepository extends JpaRepository<Hold, String> {
    @Query("select h from Hold h join fetch h.drop where h.id = :id")
    Optional<Hold> findByIdWithDrop(@Param("id") String id);

    List<Hold> findTop100ByStatusAndExpiresAtLessThanEqualOrderByExpiresAtAsc(HoldStatus status, Instant now);

    @Modifying
    @Query("update Hold h set h.status = :toStatus where h.id = :id and h.status = :fromStatus and h.expiresAt <= :now")
    int expireIfActive(@Param("id") String id, @Param("fromStatus") HoldStatus fromStatus,
                       @Param("toStatus") HoldStatus toStatus, @Param("now") Instant now);

    @Modifying
    @Query("update Hold h set h.status = :toStatus where h.id = :id and h.status = :fromStatus")
    int transition(@Param("id") String id, @Param("fromStatus") HoldStatus fromStatus,
                   @Param("toStatus") HoldStatus toStatus);
}
