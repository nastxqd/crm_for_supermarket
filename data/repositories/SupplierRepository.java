package com.shop.shop.data.repositories;

import com.shop.shop.data.entities.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    public List<Supplier> findAll();
}
