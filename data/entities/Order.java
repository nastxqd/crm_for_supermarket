package com.shop.shop.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Table(name = "PurchaseOrder")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @Column(name="oredr_id")
    private Long id;
    @Column(name="supplier_id")
    private Long supplier;
    @Column(name="user_id")
    private Long userId;
    @Column (name="order_date")
    private Date orderDate;
    @Column(name="status")
    private String status;
    @Column(name = "total_amount")
    private int amount;
    @Column (name="expected_delivery")
    private Date expectedDelivery;
    @Column(name="actual_delivery")
    private Date actualDelivery;
}
