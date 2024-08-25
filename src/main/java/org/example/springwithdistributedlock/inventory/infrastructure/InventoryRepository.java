package org.example.springwithdistributedlock.inventory.infrastructure;

import org.example.springwithdistributedlock.inventory.domain.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {

    Optional<InventoryEntity> findByLocationAndGoodsId(String location, String goodsId);
}
