package com.kibo.reservation.repository;

import com.kibo.reservation.entity.Drop;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface DropRepository extends JpaRepository<Drop, Long> {
    @Modifying
    @Query("update Drop d set d.availableUnits = d.availableUnits - :quantity where d.id = :dropId and d.availableUnits >= :quantity")
    int reserveUnits(@Param("dropId") Long dropId, @Param("quantity") int quantity);

    @Modifying
    @Query("update Drop d set d.availableUnits = d.availableUnits + :quantity where d.id = :dropId and d.availableUnits + :quantity <= d.totalUnits")
    int releaseUnits(@Param("dropId") Long dropId, @Param("quantity") int quantity);

    @Query(value = "select available_units from drops where id = :dropId", nativeQuery = true)
    Integer findAvailableUnits(@Param("dropId") Long dropId);
}
