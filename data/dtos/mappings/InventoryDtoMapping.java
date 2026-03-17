package com.shop.shop.data.dtos.mappings;

import com.shop.shop.data.dtos.InventoryDto;
import com.shop.shop.data.entities.Inventory;

public class InventoryDtoMapping {
    public static InventoryDto from(Inventory inventory){
        return new InventoryDto(inventory.getId(), inventory.getProductId(), inventory.getWarehouseId(), inventory.getQuantity(), inventory.getMinLevel(),  inventory.getMaxLevel(), inventory.getLevelUpdated());
    }
}
