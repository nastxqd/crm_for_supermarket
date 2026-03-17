package com.shop.shop.buiseness;

import com.shop.shop.data.entities.Supplier;
import com.shop.shop.data.repositories.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService implements SupplierServiceInterface {
    @Autowired
    private SupplierRepository supplierRepository;
    @Override
    public List<Supplier> getAllSuppliers(){
        return supplierRepository.findAll();
    }
}
