package org.example.springwithdistributedlock.inventory.domain;


import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class InventoryEntities {

    @Builder.Default
    private List<InventoryEntity> entities = new ArrayList<>();

    public InventoryEntities(List<InventoryEntity> entities) {
        this.entities = entities;
    }

    public InventoryEntity getLastInventory() {
        return this.entities.get(this.entities.size() - 1);
    }

}
