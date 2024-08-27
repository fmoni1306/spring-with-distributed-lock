package org.example.springwithdistributedlock.inventory.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.springwithdistributedlock.outbound.domain.OutboundEntity;

@Getter
@Entity
@Table(name = "outbound_inventory_mapping")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OutboundInventoryMappingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outbound_id", nullable = false)
    private OutboundEntity outboundEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id" ,nullable = false)
    private InventoryEntity inventoryEntity;

    private Boolean process;

    public void updateProcess(boolean process) {
        this.process = process;
    }

    public static OutboundInventoryMappingEntity from(OutboundInventoryMappingModel model) {
        OutboundInventoryMappingEntity entity = new OutboundInventoryMappingEntity();
        entity.id = model.getId();
        entity.outboundEntity = model.getOutboundEntity();
        entity.inventoryEntity = model.getInventoryEntity();
        entity.process = model.getProcess();
        return entity;
    }

    public OutboundInventoryMappingModel toModel() {
        return OutboundInventoryMappingModel.builder()
                .id(this.id)
                .outboundEntity(this.outboundEntity)
                .inventoryEntity(this.inventoryEntity)
                .process(this.process)
                .build();
    }
}
