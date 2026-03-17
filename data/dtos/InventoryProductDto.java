package com.shop.shop.data.dtos;

public record InventoryProductDto(Long productId, String productName, Integer quantity, Integer minLevel, Integer maxLevel, String status) {
}
