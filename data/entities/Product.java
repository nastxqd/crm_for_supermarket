package com.shop.shop.data.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Table(name = "Product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="product_id")
    private Long id;
    @Column(name="name")
    private String name;
    @Column(name="sku")
    private String sku;
    @Column(name="barcode")
    private String barcode;
    @Column(name="unit_of_measure")
    private String unit;
    @Column(name="default_supplier_id")
    private Long supplier;
    @Column(name="created_at")
    private Date createdAt;
    @Column(name="updated_at")
    private Date updatedAt;
    @Column(name = "category_id")
    private Long caregoryId;
    @ManyToOne(fetch = FetchType.LAZY)  // LAZY для производительности
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCaregoryId() {
        return caregoryId;
    }

    public void setCaregoryId(Long caregoryId) {
        this.caregoryId = caregoryId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSupplier(Long supplier) {
        this.supplier = supplier;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBarcode() {
        return barcode;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Long getSupplier() {
        return supplier;
    }

    public String getSku() {
        return sku;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getUnit() {
        return unit;
    }
}
