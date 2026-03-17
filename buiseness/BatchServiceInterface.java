package com.shop.shop.buiseness;

import com.shop.shop.data.dtos.BatchDto;
import com.shop.shop.data.dtos.BatchFilterDto;
import com.shop.shop.data.dtos.BatchGroupDto;
import com.shop.shop.data.entities.Batch;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BatchServiceInterface{
    public Batch craeteBatch(Batch batch);
    public void deleteBatch(Batch batch);
    public Batch getById(Long id);
    public Batch getByNumber(String number);
    public Batch getBySupplier(Long supplier);
    public List<BatchDto> getAll();
    public List<Batch> getAllByWarehouseId(Long warehouseId);
    public List<BatchGroupDto> getGroupedBatchesByWarehouse(Long warehouseId);
    public List<BatchGroupDto> getFilteredBatches(Long warehouseId, BatchFilterDto filter);

}
