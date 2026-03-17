package com.shop.shop.data.dtos;

import com.shop.shop.data.entities.Category;

import java.sql.Date;

public record ProductDto (
    Long id,
 String name,
   String barcode,
     String sku,
 String unit,
     long supplier,
        Date createdAt,
        Date updatedAt,
    Long categoryId, String categoryName
    ){

}
