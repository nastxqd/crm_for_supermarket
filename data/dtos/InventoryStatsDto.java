package com.shop.shop.data.dtos;

public record InventoryStatsDto(
        int totalProducts,
        int totalItems,
        int criticalCount,
        int excessCount
) {}