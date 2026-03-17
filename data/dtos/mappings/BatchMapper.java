package com.shop.shop.data.dtos.mappings;

import com.shop.shop.data.dtos.BatchGroupDto;
import com.shop.shop.data.dtos.BatchItemDto;
import com.shop.shop.data.entities.Batch;
import com.shop.shop.data.entities.Product;
import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BatchMapper {
    public BatchItemDto toItemDto(Batch batch) {
        if (batch == null) return null;

        // Получаем продукт из связи (он уже загружен благодаря JOIN FETCH)
        Product product = batch.getProduct();
        String productName = product != null ? product.getName() : "Товар " + batch.getProductId();

        return new BatchItemDto(
                batch.getId(),
                batch.getProductId(),
                productName,                    // теперь с названием!
                batch.getExpirydate(),
                batch.getProductionDate(),
                batch.getCostprice(),
                batch.getQuantity()
        );
    }

    // Маппинг группы записей в BatchGroupDto
    public BatchGroupDto toGroupDto(String batchNumber, List<Batch> batches) {
        if (batches == null || batches.isEmpty()) return null;

        Date createdAt = batches.get(0).getCreatedAt();

        // Преобразуем все записи группы
        List<BatchItemDto> items = batches.stream()
                .map(this::toItemDto)           // используем простой маппинг
                .collect(Collectors.toList());

        int totalQuantity = batches.stream()
                .mapToInt(Batch::getQuantity)
                .sum();

        return new BatchGroupDto(
                batchNumber,
                createdAt,
                items.size(),
                totalQuantity,
                items
        );
    }

    // Маппинг Map<String, List<Batch>> в List<BatchGroupDto>
    public List<BatchGroupDto> toGroupDtoList(Map<String, List<Batch>> groupedBatches) {
        return groupedBatches.entrySet().stream()
                .map(entry -> toGroupDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
