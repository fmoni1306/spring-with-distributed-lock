package org.example.springwithdistributedlock.inventory.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.springwithdistributedlock.outbound.domain.OutboundEntity;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id" ,nullable = false)
    private List<InventoryEntity> inventoryEntities = new ArrayList<>();
}
