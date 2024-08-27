package org.example.springwithdistributedlock.outbound.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.springwithdistributedlock.inventory.domain.OutboundInventoryMappingEntity;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "outboundEntity", fetch = FetchType.LAZY)
    @Builder.Default
    private List<OutboundInventoryMappingEntity> outboundInventoryMappingEntity = new ArrayList<>();

    private String outboundStatus;

    private Long quantity;

    private Long remainQuantity;

    public void updateStatusReady() {
        this.outboundStatus = "READY";
    }

    public void decreaseRemainQuantity(Long quantity) {
        this.remainQuantity -= quantity;
    }
}
