package com.shop.shop.buiseness;

import com.shop.shop.data.entities.Warehouse;
import com.shop.shop.data.repositories.WarehouserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class WarehouseService implements WarehouseServiceInterface{
    @Autowired
    private WarehouserRepository warehouserRepository;
    @Override
    public List<Warehouse> getAllWarehouses(){
        return warehouserRepository.findAll();
    }

    @Override
    public Optional<Warehouse> getWarehouseById(Long id) {
        return warehouserRepository.findById(id);
    }


}
