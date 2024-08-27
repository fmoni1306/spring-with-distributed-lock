package org.example.springwithdistributedlock.outbound.infrastructure;

import org.example.springwithdistributedlock.inventory.domain.OutboundInventoryMappingEntity;
import org.example.springwithdistributedlock.outbound.domain.OutboundEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OutboundInventoryMappingRepository extends JpaRepository<OutboundInventoryMappingEntity, Long> {

    @Query(
            """
            SELECT OIME
            FROM OutboundInventoryMappingEntity OIME
            JOIN FETCH OutboundEntity OE ON OE = OIME.outboundEntity
            JOIN FETCH InventoryEntity IE ON IE = OIME.inventoryEntity
            WHERE OE = :outboundEntity
            """
    )
    List<OutboundInventoryMappingEntity> findByOutboundEntity(OutboundEntity outboundEntity);
}
