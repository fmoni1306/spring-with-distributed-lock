package org.example.springwithdistributedlock.outbound.service;

import lombok.RequiredArgsConstructor;
import org.example.springwithdistributedlock.inventory.domain.InventoryEntity;
import org.example.springwithdistributedlock.inventory.domain.OutboundInventoryMappingEntity;
import org.example.springwithdistributedlock.inventory.domain.OutboundInventoryMappingModel;
import org.example.springwithdistributedlock.inventory.infrastructure.InventoryRepository;
import org.example.springwithdistributedlock.inventory.service.ManyInventoryService;
import org.example.springwithdistributedlock.outbound.domain.OutboundEntity;
import org.example.springwithdistributedlock.outbound.infrastructure.OutboundInventoryMappingRepository;
import org.example.springwithdistributedlock.outbound.infrastructure.OutboundRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ManyOutboundService {

    private final OutboundRepository outboundRepository;
    private final ManyInventoryService inventoryService;
    private final OutboundInventoryMappingRepository outboundInventoryMappingRepository;
    private final InventoryRepository inventoryRepository;

    public InventoryEntity distributedOutboundChangeWhenManyInventory(Long outboundId) {
        OutboundEntity outbound =
                outboundRepository.findById(outboundId)
                        .orElseThrow(RuntimeException::new);

        List<OutboundInventoryMappingEntity> list =  outboundInventoryMappingRepository.findByOutboundEntity(outbound);

        for (OutboundInventoryMappingEntity entity : list) {
            inventoryService.decreaseAndMovingInventoryWhenManyInventory(entity);
        }
        InventoryEntity result = inventoryRepository.findByLocationAndGoodsId("O1", "G1")
                .orElseThrow();

        outbound.updateStatusReady();

        return result;
    }

    /**
     * 도메인 모델을 사용한 분산락 업데이트
     */
    public InventoryEntity distributedOutboundChangeWhenManyInventoryWithModel(Long outboundId) {
        OutboundEntity outbound =
                outboundRepository.findById(outboundId)
                        .orElseThrow(RuntimeException::new);

        List<OutboundInventoryMappingEntity> list =  outboundInventoryMappingRepository.findByOutboundEntity(outbound);

        List<OutboundInventoryMappingModel> modelList = list.stream()
                .map(OutboundInventoryMappingEntity::toModel)
                .toList();

        for (OutboundInventoryMappingModel model : modelList) {
            inventoryService.decreaseAndMovingInventoryWhenManyInventoryWithModel(model);
        }
        InventoryEntity result = inventoryRepository.findByLocationAndGoodsId("O1", "G1")
                .orElseThrow();

        outbound.updateStatusReady();

        return result;
    }
}
