package com.shop.shop.buiseness;

import com.shop.shop.data.entities.Warehouse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface WarehouseServiceInterface {
    public List<Warehouse> getAllWarehouses();
    public Optional<Warehouse> getWarehouseById(Long id);
}
