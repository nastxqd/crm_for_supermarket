package com.shop.shop.data.repositories;

import com.shop.shop.data.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    public List<Inventory> findByWarehouseId(Long warehouseId);
    Optional<Inventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId);
    @Query("SELECT i FROM Inventory i WHERE i.warehouseId = :warehouseId")
    List<Inventory> findByWarehouseIdWithProducts(@Param("warehouseId") Long warehouseId);
    List<Inventory> findByWarehouseIdOrderByLevelUpdatedDesc(Long warehouseId);
    @Procedure(name = "UpdateInventory")
    void updateInventory(
            @Param("p_product_id") Long productId,
            @Param("p_warehouse_id") Long warehouseId,
            @Param("p_quantity_change") Integer quantityChange,
            @Param("p_min_stock") Integer minStock,
            @Param("p_max_stock") Integer maxStock
    );


}
