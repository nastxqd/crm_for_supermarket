package com.shop.shop.data.repositories;

import com.shop.shop.data.dtos.ProductDto;
import com.shop.shop.data.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    public Product getProductById(Long id);
    public List<Product> findAll();
    public void delete(Product product);
    public Product save(Product product);
    //public Product changeProduct(Product product);
}
