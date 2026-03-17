package com.shop.shop.data.repositories;

import java.sql.Date;

public record WarehouserDto(Long id, String name, String address, String type, String responsiblePerson, double capacity, boolean isActive, Date createdAt) {
}
