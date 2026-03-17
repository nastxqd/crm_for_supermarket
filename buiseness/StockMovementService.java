package com.shop.shop.buiseness;

import com.shop.shop.data.dtos.StockMovementDto;
import com.shop.shop.data.dtos.mappings.StockMovementDtoMapping;
import com.shop.shop.data.entities.StockMovement;
import com.shop.shop.data.repositories.StockMovementRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class StockMovementService implements StockMovemenServiceInterface{
    private StockMovementRepository stockMovementRepository;
    public StockMovementService(StockMovementRepository stockMovementRepository){
        this.stockMovementRepository = stockMovementRepository;
    }
    @Override
    public StockMovementDto getById(Long id) {
        if (id == null) {
            return null;
        }
        return StockMovementDtoMapping.from(stockMovementRepository.findById(id).orElse(null));
    }

    @Override
    public StockMovement getByProductId(Long productId) {
        return stockMovementRepository.getByProductId(productId);
    }

    @Override
    public StockMovement getByFromId(Long fromId) {
        return stockMovementRepository.getByFromId(fromId);
    }

    @Override
    public StockMovement getByToId(Long toId) {
        return stockMovementRepository.getByToId(toId);
    }

    @Override
    public StockMovement getByDate(Date date) {
        return stockMovementRepository.getByMovementDate(date);
    }

    @Override
    public StockMovement getByReference(String reference) {
        return stockMovementRepository.getByReference(reference);
    }

    @Override
    public void delete(StockMovement stockMovement) {
        stockMovementRepository.delete(stockMovement);
    }

    @Override
    public StockMovement createStockMovement(StockMovement stockMovement) {
        return stockMovementRepository.save(stockMovement);
    }
    @Override
    public List<StockMovement> getAll(){
        return stockMovementRepository.findAll();
    }
}
