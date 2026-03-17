package com.shop.shop.controllers;

import com.shop.shop.buiseness.BatchServiceInterface;
import com.shop.shop.data.dtos.BatchDto;
import com.shop.shop.data.dtos.BatchFilterDto;
import com.shop.shop.data.dtos.BatchGroupDto;
import com.shop.shop.data.dtos.mappings.BatchDtoMapping;
import com.shop.shop.data.entities.Batch;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/batches")
public class BatchController {

    private BatchServiceInterface batchServiceInterface;

    public BatchController(BatchServiceInterface batchServiceInterface) {
        this.batchServiceInterface = batchServiceInterface;
    }
    @PostMapping ("/addBatch")
    public ResponseEntity<Batch> addBatch(@RequestBody Batch batch) {
        return ResponseEntity.ok(batchServiceInterface.craeteBatch(batch));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatchDto> getBatchById(@PathVariable Long id) {
        return ResponseEntity.ok(BatchDtoMapping.from(batchServiceInterface.getById(id)));
    }

    @GetMapping("/number/{number}")
    public ResponseEntity<Batch> getBatchByNumber(@PathVariable String number) {
        return ResponseEntity.ok(batchServiceInterface.getByNumber(number));
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<Batch> getBatchBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(batchServiceInterface.getBySupplier(supplierId));
    }

    @PostMapping("/batch/delete")
    @PreAuthorize("hasAnyRole('SENIOR_STOREKEEPER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<String> deleteBatch(@RequestBody Batch batch) {
        try {
            batchServiceInterface.deleteBatch(batch);
            return ResponseEntity.ok("Партия успешно удалена");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка при удалении партии");
        }
    }

    @GetMapping
    public ResponseEntity<List<BatchDto>> getAllBatches() {
        return ResponseEntity.ok(batchServiceInterface.getAll());
    }
    @GetMapping("/batches/byWarehouse")
    public ResponseEntity<List<Batch>> getAllByWarehouseId(@RequestBody Long warehouseId){
        return ResponseEntity.ok(batchServiceInterface.getAllByWarehouseId(warehouseId));
    }
    @GetMapping("/warehouse/{warehouseId}/grouped")
    public ResponseEntity<List<BatchGroupDto>> getGroupedBatchesByWarehouse(
            @PathVariable Long warehouseId) {

        List<BatchGroupDto> groupedBatches = batchServiceInterface.getGroupedBatchesByWarehouse(warehouseId);
        return ResponseEntity.ok(groupedBatches);
    }
    @PostMapping("/warehouse/{warehouseId}/filtered")
    public ResponseEntity<List<BatchGroupDto>> getFilteredBatches(
            @PathVariable Long warehouseId,
            @RequestBody BatchFilterDto filter) {
        List<BatchGroupDto> filteredBatches = batchServiceInterface.getFilteredBatches(warehouseId, filter);
        return ResponseEntity.ok(filteredBatches);
    }
    @GetMapping("/warehouse/{warehouseId}/search")
    public ResponseEntity<List<BatchGroupDto>> searchBatches(
            @PathVariable Long warehouseId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo) {

        BatchFilterDto filter = new BatchFilterDto();
        filter.setSupplierid(supplierId);
        filter.setProductId(productId);
        filter.setDateFrom(dateFrom);
        filter.setDateTo(dateTo);

        List<BatchGroupDto> filteredBatches = batchServiceInterface.getFilteredBatches(warehouseId, filter);
        return ResponseEntity.ok(filteredBatches);
    }
}