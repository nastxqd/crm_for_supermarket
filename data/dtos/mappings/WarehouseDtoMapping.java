package com.shop.shop.data.dtos.mappings;

import com.shop.shop.data.entities.Warehouse;
import com.shop.shop.data.repositories.WarehouserDto;

public class WarehouseDtoMapping {
    public static WarehouserDto from(Warehouse warehouse){
        return new WarehouserDto(warehouse.getId(), warehouse.getName(), warehouse.getAddress(), warehouse.getType(), warehouse.getResponsiblePerson(), warehouse.getCapacity(), warehouse.isActive(), warehouse.getCreatedAt());
    }
}
