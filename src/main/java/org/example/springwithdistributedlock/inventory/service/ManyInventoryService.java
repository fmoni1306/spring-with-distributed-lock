package org.example.springwithdistributedlock.inventory.service;

import lombok.RequiredArgsConstructor;
import org.example.springwithdistributedlock.common.annotation.DistributedLock;
import org.example.springwithdistributedlock.common.exception.ForceException;
import org.example.springwithdistributedlock.inventory.domain.InventoryEntity;
import org.example.springwithdistributedlock.inventory.domain.OutboundInventoryMappingEntity;
import org.example.springwithdistributedlock.inventory.domain.OutboundInventoryMappingModel;
import org.example.springwithdistributedlock.inventory.infrastructure.InventoryRepository;
import org.example.springwithdistributedlock.outbound.domain.OutboundEntity;
import org.example.springwithdistributedlock.outbound.infrastructure.OutboundInventoryMappingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ManyInventoryService {

    private final InventoryRepository inventoryRepository;
    private final OutboundInventoryMappingRepository outboundInventoryMappingRepository;

    @DistributedLock(key = "#entity.getInventoryEntity.getDecreaseQuantityKey")
    public InventoryEntity decreaseAndMovingInventoryWhenManyInventory(OutboundInventoryMappingEntity entity) {

        OutboundEntity outboundEntity = entity.getOutboundEntity();
        InventoryEntity inventoryEntity = entity.getInventoryEntity();

        Long qunatity = outboundEntity.getRemainQuantity();

        if (outboundEntity.getRemainQuantity() > inventoryEntity.getQuantity()) {
            qunatity = inventoryEntity.getQuantity();
        }

        outboundEntity.decreaseRemainQuantity(qunatity);

        InventoryEntity newInventory = InventoryEntity.builder()
                .location("O1")
                .goodsId("G1")
                .goodsName("사과")
                .locationType("OUT")
                .quantity(qunatity)
                .build();

        Optional<InventoryEntity> existInventory = inventoryRepository.findByLocationAndGoodsId(newInventory.getLocation(), newInventory.getGoodsId());

        if (existInventory.isPresent()) {
            newInventory = existInventory.get();
            newInventory.increaseQuantity(qunatity);
        }

        inventoryRepository.save(newInventory);

        inventoryEntity.decreaseQuantity(qunatity);
        entity.updateProcess(true);

        if (outboundEntity.getRemainQuantity() == 1) {
            // 강제로 1개 남았을때 예외 발생
            throw new ForceException();
        }
        return newInventory;
    }

    @DistributedLock(key = "#model.getInventoryEntity.getDecreaseQuantityKey")
    public void decreaseAndMovingInventoryWhenManyInventoryWithModel(OutboundInventoryMappingModel model) {
        OutboundEntity outboundEntity = model.getOutboundEntity();
        InventoryEntity inventoryEntity = model.getInventoryEntity();

        Long qunatity = outboundEntity.getRemainQuantity();

        if (outboundEntity.getRemainQuantity() > inventoryEntity.getQuantity()) {
            qunatity = inventoryEntity.getQuantity();
        }

        outboundEntity.decreaseRemainQuantity(qunatity);

        InventoryEntity newInventory = InventoryEntity.builder()
                .location("O1")
                .goodsId("G1")
                .goodsName("사과")
                .locationType("OUT")
                .quantity(qunatity)
                .build();

        Optional<InventoryEntity> existInventory = inventoryRepository.findByLocationAndGoodsId(newInventory.getLocation(), newInventory.getGoodsId());

        if (existInventory.isPresent()) {
            newInventory = existInventory.get();
            newInventory.increaseQuantity(qunatity);
        }

        inventoryRepository.save(newInventory);

        inventoryEntity.decreaseQuantity(qunatity);
        model.updateProcess(true);

        outboundInventoryMappingRepository.save(OutboundInventoryMappingEntity.from(model));

        if (outboundEntity.getRemainQuantity() == 1) {
            // 강제로 1개 남았을때 예외 발생
            throw new ForceException();
        }
    }
}
