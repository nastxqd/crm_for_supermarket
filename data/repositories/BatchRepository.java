package com.shop.shop.data.repositories;

import com.shop.shop.data.entities.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
    public Batch save(Batch batch);
    public void delete(Batch batch);
    public Batch getById(Long id);
    public Batch getByBatchNumber(String number);
    public Batch getBySupplier(Long supplier);
    public List<Batch> findAll();
    public List<Batch> findByWarehouseId(Long warehouseid);
    @Query("SELECT b FROM Batch b " +
            "LEFT JOIN FETCH b.product " +
            "WHERE b.warehouseId = :warehouseId " +
            "ORDER BY b.batchNumber")
    List<Batch> findByWarehouseIdWithProducts(@Param("warehouseId") Long warehouseId);
    @Query("SELECT b FROM Batch b " +
            "LEFT JOIN FETCH b.product " +
            "WHERE b.warehouseId = :warehouseId " +
            "AND (:supplierId IS NULL OR b.supplier = :supplierId) " +
            "AND (:productId IS NULL OR b.productId = :productId) " +
            "AND (cast(:dateFrom as date) IS NULL OR b.createdAt >= :dateFrom) " +
            "AND (cast(:dateTo as date) IS NULL OR b.createdAt <= :dateTo) " +
            "ORDER BY b.createdAt DESC")
    List<Batch> findByWarehouseIdWithFilters(
            @Param("warehouseId") Long warehouseId,
            @Param("supplierId") Long supplierId,
            @Param("productId") Long productId,
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo
    );
    @Query("SELECT b FROM Batch b WHERE b.warehouseId = :warehouseId AND b.productId = :productId AND b.expirydate >= CURRENT_DATE AND b.quantity > 0 ORDER BY b.expirydate")
    List<Batch> findActiveBatchesByProduct(@Param("warehouseId") Long warehouseId, @Param("productId") Long productId);
}
