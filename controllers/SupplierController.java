package com.shop.shop.controllers;

import com.shop.shop.buiseness.SupplierServiceInterface;
import com.shop.shop.data.entities.Supplier;
import org.hibernate.annotations.Array;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    @Autowired
    private SupplierServiceInterface supplierServiceInterface;
    @GetMapping("/getAllSuppliers")
    public List<Supplier> getAllSuppliers(){
        return supplierServiceInterface.getAllSuppliers();
    }
}
