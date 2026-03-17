package com.shop.shop.data.dtos;

import java.sql.Date;

public record SupplierDto(Long id,
                          String name,
                          String contactPerson,
                          String email,
                          String phone,
                          String address,
                          String taxId,
                          double rating,
                          Date createdAt,
                          Date updatedAt) {
}
