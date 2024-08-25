package org.example.springwithdistributedlock.outbound.service;

import lombok.RequiredArgsConstructor;
import org.example.springwithdistributedlock.inventory.domain.InventoryEntity;
import org.example.springwithdistributedlock.inventory.infrastructure.InventoryRepository;
import org.example.springwithdistributedlock.inventory.service.InventoryService;
import org.example.springwithdistributedlock.outbound.domain.OutboundEntity;
import org.example.springwithdistributedlock.outbound.infrastructure.OutboundRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OutboundService {

    private final OutboundRepository outboundRepository;
    private final InventoryRepository inventoryRepository;

    private final InventoryService inventoryService;

    /**
     * 분산락 미적용
     * @param outboundId
     * @return
     */
    public InventoryEntity notDistributedOutboundChange(Long outboundId) {
        OutboundEntity outbound =
                outboundRepository.findById(outboundId)
                        .orElseThrow(RuntimeException::new);

        InventoryEntity newInventory = this.decreaseAndMovingInventory(outbound);

        outbound.updateStatusReady();

        // 편의상 Dto 없이 Entity 바로 반환
        return newInventory;
    }

    /**
     * 분산락 적용
     * @param outboundId
     * @return
     */
    public InventoryEntity distributedOutboundChange(Long outboundId) {
        OutboundEntity outbound =
                outboundRepository.findById(outboundId)
                        .orElseThrow(RuntimeException::new);

        InventoryEntity newInventory = inventoryService.decreaseAndMovingInventory(outbound);

        outbound.updateStatusReady();

        // 편의상 Dto 없이 Entity 바로 반환
        return newInventory;
    }

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
