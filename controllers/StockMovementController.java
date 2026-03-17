package com.shop.shop.controllers;

import com.shop.shop.buiseness.StockMovemenServiceInterface;
import com.shop.shop.data.dtos.StockMovementDto;
import com.shop.shop.data.entities.StockMovement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StockMovementController {
    @Autowired
    private StockMovemenServiceInterface stockMovemenServiceInterface;

    @GetMapping("/stock-movement")  // Оставляем как было
    public StockMovementDto getById(@RequestParam(required = false) Long id){ // Добавляем required=false
        if (id == null) {
            return null; // Просто возвращаем null, если id нет
        }
        return stockMovemenServiceInterface.getById(id);
    }

    @PostMapping("/stock-movement/add")
    void addStockMovement(@RequestBody StockMovement stockMovement){
        stockMovemenServiceInterface.createStockMovement(stockMovement);
    }

    @PostMapping("/stock-movement/delete")
    void deleteStockMovement(@RequestBody StockMovement stockMovement){
        stockMovemenServiceInterface.delete(stockMovement);
    }

    @GetMapping("/stock-movements")
    List<StockMovement> getAll(){
        return stockMovemenServiceInterface.getAll();
    }
}