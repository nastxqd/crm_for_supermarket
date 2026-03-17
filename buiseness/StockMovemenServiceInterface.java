package com.shop.shop.buiseness;

import com.shop.shop.data.dtos.StockMovementDto;
import com.shop.shop.data.entities.StockMovement;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public interface StockMovemenServiceInterface {
    StockMovementDto getById(Long id);
    StockMovement getByProductId(Long productId);
    StockMovement getByFromId(Long fromId);
    StockMovement getByToId(Long toId);
    StockMovement getByDate(Date date);
    StockMovement getByReference(String reference);
    void delete(StockMovement stockMovement);
    StockMovement createStockMovement(StockMovement stockMovement);
    List<StockMovement> getAll();
}
