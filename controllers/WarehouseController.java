package com.shop.shop.controllers;

import com.shop.shop.buiseness.SupplierServiceInterface;
import com.shop.shop.buiseness.WarehouseService;
import com.shop.shop.data.entities.Supplier;
import com.shop.shop.data.entities.Warehouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {
    @Autowired
    private WarehouseService warehouseService;
    @GetMapping("/getAllWarehouses")
    public List<Warehouse> getAllSuppliers(){
        return warehouseService.getAllWarehouses();
    }
    @GetMapping("/findWarehouse")
    public ResponseEntity getWarehouseById(@RequestParam Long id){
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }
}