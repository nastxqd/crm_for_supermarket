package com.shop.shop.data.dtos.mappings;

import com.shop.shop.data.dtos.StockMovementDto;
import com.shop.shop.data.entities.StockMovement;

public class StockMovementDtoMapping {
    public static StockMovementDto from(StockMovement stockMovement){
        return new StockMovementDto(stockMovement.getId(), stockMovement.getProductId(), stockMovement.getFromId(), stockMovement.getToId(), stockMovement.getMovementDate(), stockMovement.getType(), stockMovement.getQuantity(), stockMovement.getReason(), stockMovement.getReference());
    }
}
