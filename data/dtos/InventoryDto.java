package com.shop.shop.data.dtos;

import java.sql.Date;

public record InventoryDto(Long id, Long productId, Long warehouseId, int quantity, int minLevel, int maxLevel, Date levelUpdated) {

}
