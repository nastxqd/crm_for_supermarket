package com.shop.shop.data.repositories;

import com.shop.shop.data.entities.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    public StockMovement getById(Long id);
    public StockMovement getByProductId(Long productId);
    public StockMovement getByFromId(Long fromId);
    public StockMovement getByToId(Long toId);
    public StockMovement getByType(String type);
    StockMovement getByReference(String reference);
    StockMovement save(StockMovement stockMovement);
    void delete(StockMovement stockMovement);
    StockMovement getByMovementDate(Date date);
    List<StockMovement> findAll();
}
