package com.shop.shop.data.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Table (name = "StockMovement")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class StockMovement {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "movement_id")
    private Long id;
    @Column(name = "product_id")
    private Long productId;
    @Column( name = "from_warehouse_id")
    private Long fromId;
    @Column(name = "to_warehouse_id")
    private Long toId;
    @Column(name = "movement_date")
    private Date movementDate;
    @Column(name = "type")
    private String type;
    @Column (name = "quantity")
    private Integer quantity;
    @Column(name = "reason")
    private String reason;
    @Column (name = "reference_number")
    private String reference;


    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getFromId() {
        return fromId;
    }

    public Long getToId() {
        return toId;
    }

    public Date getMovementDate() {
        return movementDate;
    }

    public String getType() {
        return type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getReason() {
        return reason;
    }

    public String getReference() {
        return reference;
    }
}
