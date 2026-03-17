package com.shop.shop.data.dtos;

import java.sql.Date;
import java.util.List;

public record BatchGroupDto(String batchNumber, Date createdAt, int totalItems, int totalQuantity, List<BatchItemDto> items) {
}
