package com.shop.shop.data.repositories;

import com.shop.shop.data.entities.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouserRepository extends JpaRepository<Warehouse, Long> {
    public List<Warehouse> findAll();
    public Optional<Warehouse> findById(Long id);

}
