package org.example.springwithdistributedlock.inventory.service;

import lombok.RequiredArgsConstructor;
import org.example.springwithdistributedlock.common.annotation.DistributedLock;
import org.example.springwithdistributedlock.inventory.domain.InventoryEntity;
import org.example.springwithdistributedlock.inventory.infrastructure.InventoryRepository;
import org.example.springwithdistributedlock.outbound.domain.OutboundEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @DistributedLock(key = "#outbound.getInventoryEntity.getDecreaseQuantityKey")
    public InventoryEntity decreaseAndMovingInventory(OutboundEntity outbound) {
        InventoryEntity newInventory = InventoryEntity.builder()
                .location("O1")
                .goodsId("G1")
                .goodsName("사과")
                .locationType("OUT")
                .quantity(outbound.getQuantity())
                .build();

        Optional<InventoryEntity> existInventory = inventoryRepository.findByLocationAndGoodsId(newInventory.getLocation(), newInventory.getGoodsId());

        if (existInventory.isPresent()) {
            newInventory = existInventory.get();
            newInventory.increaseQuantity(outbound.getQuantity());
        }

        inventoryRepository.save(newInventory);

        InventoryEntity inventory = outbound.getInventoryEntity();

        inventory.decreaseQuantity(outbound.getQuantity());
        return newInventory;
    }
}
