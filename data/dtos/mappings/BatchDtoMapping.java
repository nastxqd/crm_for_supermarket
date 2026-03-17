package com.shop.shop.data.dtos.mappings;

import com.shop.shop.data.dtos.BatchDto;
import com.shop.shop.data.entities.Batch;

public class BatchDtoMapping {
    public static BatchDto from(Batch batch){
        return new BatchDto(batch.getId(), batch.getProductId(), batch.getBatchNumber(), batch.getExpirydate(), batch.getProductionDate(), batch.getCostprice(), batch.getSupplier(), batch.getWarehouseId(), batch.getQuantity());
    }
}
