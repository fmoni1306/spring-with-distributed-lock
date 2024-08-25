package org.example.springwithdistributedlock.outbound.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.springwithdistributedlock.inventory.domain.InventoryEntity;

@Getter
@Entity
@Table(name = "outbound")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OutboundEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private InventoryEntity inventoryEntity;

    private String outboundStatus;

    private Long quantity;

    public void updateStatusReady() {
        this.outboundStatus = "READY";
    }
}
