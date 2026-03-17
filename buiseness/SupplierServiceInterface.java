package com.shop.shop.buiseness;

import com.shop.shop.data.entities.Supplier;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface SupplierServiceInterface {
    public List<Supplier> getAllSuppliers();
}
