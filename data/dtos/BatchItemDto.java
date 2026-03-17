package com.shop.shop.data.dtos;

import java.sql.Date;

public record BatchItemDto(Long batchId, Long productId, String productName,  Date expiryDate, Date productionDate, double costPrice, int quantity) {
}
