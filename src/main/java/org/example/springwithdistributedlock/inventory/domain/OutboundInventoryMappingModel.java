package org.example.springwithdistributedlock.inventory.domain;

import lombok.Builder;
import lombok.Getter;
import org.example.springwithdistributedlock.outbound.domain.OutboundEntity;

@Getter
@Builder
public class OutboundInventoryMappingModel {
    private Long id;
    private OutboundEntity outboundEntity;
    private InventoryEntity inventoryEntity;
    private Boolean process;
    public void updateProcess(boolean process) {
        this.process = process;
    }
}
