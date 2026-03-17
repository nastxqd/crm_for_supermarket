package com.shop.shop.data.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Table(name = "Batch")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Long id;
    @Column(name = "product_id")
    private Long productId;
    @Column (name = "batch_number")
    private String batchNumber;
    @Column (name = "expiry_date")
    private Date expirydate;
    @Column (name = "production_date")
    private Date productionDate;
    @Column (name = "cost_price")
    private double costprice;
    @Column (name = "supplier_batch_id")
    private Long supplier;
    @Column(name = "warehouse_id")
    private Long warehouseId;
    @Column(name = "quantity")
    private int quantity;
    @Column (name="created_at")
    private Date createdAt;
    @ManyToOne(fetch = FetchType.LAZY)  // LAZY для производительности
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setQuantity (int quantity){
        this.quantity = quantity;
    }
    public int getQuantity(){return quantity;}

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public void setExpirydate(Date expirydate) {
        this.expirydate = expirydate;
    }

    public void setProductionDate(Date productionDate) {
        this.productionDate = productionDate;
    }

    public void setCostprice(double costprice) {
        this.costprice = costprice;
    }

    public void setSupplier(Long supplier) {
        this.supplier = supplier;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public Date getExpirydate() {
        return expirydate;
    }

    public Date getProductionDate() {
        return productionDate;
    }

    public double getCostprice() {
        return costprice;
    }

    public Long getSupplier() {
        return supplier;
    }
    public Long getWarehouseId(){return warehouseId;}
    public void setWarehouseId(Long warehouseId){this.warehouseId=warehouseId;}
}
