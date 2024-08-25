package org.example.springwithdistributedlock.inventory.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "inventory")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class InventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;

    private String locationType;

    private String goodsId;

    private String goodsName;

    private Long quantity;

    public void decreaseQuantity(Long quantity) {
        this.quantity -= quantity;
    }

    public void increaseQuantity(Long quantity) {
        this.quantity += quantity;
    }

    public String getDecreaseQuantityKey() {
        return this.location + "-" + this.goodsId;
    }

}
