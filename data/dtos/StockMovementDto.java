package com.shop.shop.data.dtos;

import java.sql.Date;

public record StockMovementDto(Long id, Long productId, Long fromId, Long toId, Date movementDate, String type, int quantity, String reason, String reference) {
}
