package com.shop.shop.data.dtos;

import java.util.List;

public record InventoryDetailDto(
        Long productId,
        String productName,
        Integer totalQuantity,
        Integer minLevel,
        Integer maxLevel,
        List<BatchDto> batches
) {
}
