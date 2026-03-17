package com.shop.shop.data.dtos.mappings;

import com.shop.shop.data.dtos.SupplierDto;
import com.shop.shop.data.entities.Supplier;

public class SupplierDtoMapping {
    public static SupplierDto from(Supplier supplier){
        return new SupplierDto(supplier.getId(), supplier.getName(), supplier.getContactPerson(), supplier.getEmail(), supplier.getPhone(), supplier.getAddress(), supplier.getTaxId(), supplier.getRating(), supplier.getCreatedAt(), supplier.getUpdatedAt());
    }
}
