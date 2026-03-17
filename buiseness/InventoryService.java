package com.shop.shop.buiseness;

import com.shop.shop.data.dtos.BatchDto;
import com.shop.shop.data.dtos.InventoryDetailDto;
import com.shop.shop.data.dtos.InventoryProductDto;
import com.shop.shop.data.dtos.mappings.BatchDtoMapping;
import com.shop.shop.data.entities.Batch;
import com.shop.shop.data.entities.Inventory;
import com.shop.shop.data.entities.Product;
import com.shop.shop.data.repositories.BatchRepository;
import com.shop.shop.data.repositories.InventoryRepository;
import com.shop.shop.data.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ========== СУЩЕСТВУЮЩИЕ МЕТОДЫ ==========

    public List<InventoryProductDto> getInventoryByWarehouse(Long warehouseId) {
        List<Inventory> inventories = inventoryRepository.findByWarehouseId(warehouseId);

        List<InventoryProductDto> result = new ArrayList<>();

        for (Inventory inv : inventories) {
            String productName = productRepository.findById(inv.getProductId())
                    .map(Product::getName)
                    .orElse("Товар " + inv.getProductId());

            List<Batch> batches = batchRepository.findByWarehouseIdWithFilters(warehouseId, null, inv.getProductId(), null, null);

            String status = determineOverallStatus(batches, inv.getQuantity(), inv.getMinLevel(), inv.getMaxLevel());

            result.add(new InventoryProductDto(
                    inv.getProductId(),
                    productName,
                    inv.getQuantity(),
                    inv.getMinLevel(),
                    inv.getMaxLevel(),
                    status
            ));
        }

        return result;
    }

    public InventoryDetailDto getProductDetail(Long warehouseId, Long productId) {
        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId).orElse(null);
        if (inventory == null) return null;

        String productName = productRepository.findById(productId).map(p -> p.getName()).orElse("Товар " + productId);

        // Получаем ТОЛЬКО активные партии (срок годности >= сегодня)
        List<Batch> activeBatches = batchRepository.findActiveBatchesByProduct(warehouseId, productId);

        List<BatchDto> batchDtos = activeBatches.stream()
                .map(BatchDtoMapping::from)
                .collect(Collectors.toList());

        return new InventoryDetailDto(
                productId,
                productName,
                inventory.getQuantity(),
                inventory.getMinLevel(),
                inventory.getMaxLevel(),
                batchDtos
        );
    }

    private String determineOverallStatus(List<Batch> batches, int quantity, int min, int max) {
        String quantityStatus;
        if (quantity < min) {
            quantityStatus = "⚠️ Ниже минимума";
        } else if (quantity > max) {
            quantityStatus = "⚠️ Выше максимума";
        } else {
            quantityStatus = "✅ Норма";
        }

        if (batches.isEmpty()) {
            return quantityStatus;
        }

        boolean hasExpired = false;
        boolean hasExpiringSoon = false;

        for (Batch batch : batches) {
            String batchStatus = determineBatchStatus(batch.getExpirydate());
            if (batchStatus.contains("❌ ПРОСРОЧЕН") || batchStatus.contains("❌ Истекает сегодня")) {
                hasExpired = true;
                break;
            } else if (batchStatus.contains("⚠️ Скоро истекает")) {
                hasExpiringSoon = true;
            }
        }

        if (hasExpired) {
            return "❌ Есть просрочка";
        } else if (hasExpiringSoon) {
            return "⚠️ Есть скоро истекающие";
        } else {
            return quantityStatus;
        }
    }

    private String determineBatchStatus(Date expiryDate) {
        if (expiryDate == null) return "✅ Свежий";

        LocalDate today = LocalDate.now();
        LocalDate expiry = expiryDate.toLocalDate();

        long daysUntilExpiry = ChronoUnit.DAYS.between(today, expiry);

        if (daysUntilExpiry < 0) {
            return "❌ ПРОСРОЧЕН";
        } else if (daysUntilExpiry == 0) {
            return "❌ Истекает сегодня";
        } else if (daysUntilExpiry <= 3) {
            return "⚠️ Скоро истекает";
        } else {
            return "✅ Свежий";
        }
    }
    public String updateInventory(Long productId, Long warehouseId, Integer quantityChange, Integer minStock, Integer maxStock) {
        try {
            inventoryRepository.updateInventory(productId, warehouseId, quantityChange, minStock, maxStock);
            return "✅ Процедура выполнена";
        } catch (Exception e) {
            return "❌ Ошибка: " + e.getMessage();
        }
    }


}