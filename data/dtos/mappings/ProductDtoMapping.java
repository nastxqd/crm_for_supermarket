package com.shop.shop.data.dtos.mappings;

import com.shop.shop.data.dtos.ProductDto;
import com.shop.shop.data.entities.Category;
import com.shop.shop.data.entities.Product;

public class ProductDtoMapping {
    public static ProductDto from(Product product){
        Category category = product.getCategory();
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getBarcode(),
                product.getUnit(),
                product.getSupplier(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                category != null ? category.getId() : null,
                category != null ? category.getName() : null

        );
    }
}