package com.shop.shop.data.dtos;

import java.sql.Date;

public record BatchDto(Long id,
                       Long productId,
                       String batchNumber,
                       Date expireDate,
                       Date productionDate,
                       double costprice,
                       Long supplierBatchId,
                       Long warehouseId,
                       int quantity){
}
