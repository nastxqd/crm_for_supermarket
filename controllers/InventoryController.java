package com.shop.shop.controllers;

import com.shop.shop.buiseness.InventoryService;
import com.shop.shop.data.dtos.InventoryDetailDto;
import com.shop.shop.data.dtos.InventoryProductDto;
import com.shop.shop.data.dtos.InventoryStatsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<InventoryProductDto>> getInventoryByWarehouse(
            @PathVariable Long warehouseId) {

        if (warehouseId == null || warehouseId <= 0) {
            return ResponseEntity.badRequest().build();
        }

        List<InventoryProductDto> inventory = inventoryService.getInventoryByWarehouse(warehouseId);

        if (inventory.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(inventory);
    }

    @GetMapping("/warehouse/{warehouseId}/product/{productId}")
    public ResponseEntity<InventoryDetailDto> getProductDetail(
            @PathVariable Long warehouseId,
            @PathVariable Long productId) {

        if (warehouseId == null || warehouseId <= 0 ||
                productId == null || productId <= 0) {
            return ResponseEntity.badRequest().build();
        }

        InventoryDetailDto detail = inventoryService.getProductDetail(warehouseId, productId);

        if (detail == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(detail);
    }

    @GetMapping("/warehouse/{warehouseId}/stats")
    public ResponseEntity<InventoryStatsDto> getWarehouseStats(@PathVariable Long warehouseId) {
        List<InventoryProductDto> inventory = inventoryService.getInventoryByWarehouse(warehouseId);

        int totalProducts = inventory.size();
        int totalItems = inventory.stream().mapToInt(InventoryProductDto::quantity).sum();
        int criticalCount = (int) inventory.stream()
                .filter(item -> item.quantity() < item.minLevel()).count();
        int excessCount = (int) inventory.stream()
                .filter(item -> item.quantity() > item.maxLevel()).count();

        InventoryStatsDto stats = new InventoryStatsDto(
                totalProducts,
                totalItems,
                criticalCount,
                excessCount
        );

        return ResponseEntity.ok(stats);
    }
    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('STOREKEEPER', 'MANAGER', 'ADMIN', 'SENIOR_STOREKEEPER')")
    public String updateInventory(
            @RequestParam Long productId,
            @RequestParam Long warehouseId,
            @RequestParam Integer quantityChange,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock) {

        return inventoryService.updateInventory(productId, warehouseId, quantityChange, minStock, maxStock);
    }
}