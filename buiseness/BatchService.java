package com.shop.shop.buiseness;

import com.shop.shop.data.dtos.BatchDto;
import com.shop.shop.data.dtos.BatchFilterDto;
import com.shop.shop.data.dtos.BatchGroupDto;
import com.shop.shop.data.dtos.mappings.BatchDtoMapping;
import com.shop.shop.data.dtos.mappings.BatchMapper;
import com.shop.shop.data.entities.Batch;
import com.shop.shop.data.repositories.BatchRepository;
import com.shop.shop.data.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BatchService implements BatchServiceInterface{
    @Autowired
private BatchRepository batchRepository;
    private final BatchMapper batchMapper;
    private final ProductRepository productRepository;
    private final JdbcTemplate jdbcTemplate;
public BatchService(BatchRepository batchRepository, BatchMapper batchMapper, ProductRepository productRepository, JdbcTemplate jdbcTemplate){
    this.batchRepository=batchRepository;
    this.batchMapper = batchMapper;
    this.productRepository = productRepository;
    this.jdbcTemplate = jdbcTemplate;
}
    @Override
    public Batch craeteBatch(Batch batch) {
        return batchRepository.save(batch);
    }

    @Override
    public void deleteBatch(Batch batch) {
batchRepository.delete(batch);
    }
    public Batch getById(Long id){
        return batchRepository.getById(id);
    }
    public Batch getByNumber(String number){
        return batchRepository.getByBatchNumber(number);
    }
    public Batch getBySupplier(Long supplier){
        return batchRepository.getBySupplier(supplier);
    }
    @Override
    public List<BatchDto> getAll() {
        return batchRepository.findAll().stream()
                .map(BatchDtoMapping::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<Batch> getAllByWarehouseId(Long warehouseId) {
        return batchRepository.findByWarehouseId(warehouseId);
    }
    @Override
    @Transactional
    public List<BatchGroupDto> getGroupedBatchesByWarehouse(Long warehouseId) {

        // 1. Получаем все записи для склада
        List<Batch> batches = batchRepository.findByWarehouseIdWithProducts(warehouseId);
        if (batches.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Группируем по batchNumber
        Map<String, List<Batch>> groupedByNumber = batches.stream()
                .collect(Collectors.groupingBy(Batch::getBatchNumber));

        // 3. Используем маппер для преобразования
        return batchMapper.toGroupDtoList(groupedByNumber);
    }
    @Transactional
    public List<BatchGroupDto> getFilteredBatches(Long warehouseId, BatchFilterDto filter) {

        // Используем новый метод с фильтрами
        List<Batch> batches = batchRepository.findByWarehouseIdWithFilters(
                warehouseId,
                filter.getSupplierid(),
                filter.getProductId(),
                filter.getDateFrom(),
                filter.getDateTo()
        );

        return groupAndMapBatches(batches);
    }
    private List<BatchGroupDto> groupAndMapBatches(List<Batch> batches) {
        if (batches.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, List<Batch>> groupedByNumber = batches.stream()
                .collect(Collectors.groupingBy(Batch::getBatchNumber));

        return batchMapper.toGroupDtoList(groupedByNumber);
    }


}
