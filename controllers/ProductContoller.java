package com.shop.shop.controllers;

import com.shop.shop.buiseness.ProductServiceInterface;
import com.shop.shop.data.dtos.ProductDto;
import com.shop.shop.data.dtos.mappings.ProductDtoMapping;
import com.shop.shop.data.entities.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductContoller {

    private ProductServiceInterface productServiceInterface;

    public ProductContoller(ProductServiceInterface productServiceInterface) {
        this.productServiceInterface = productServiceInterface;
    }

    @GetMapping ("/getAllProducts")
    public List<ProductDto> getAllProducts() {
        List<ProductDto> products = productServiceInterface.getAllProducts().stream()
                .map(ProductDtoMapping::from)
                .collect(Collectors.toList());
        return products;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        Product product = productServiceInterface.getById(id);
        if (product != null) {
            return ResponseEntity.ok(ProductDtoMapping.from(product));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('STOREKEEPER')")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product savedProduct = productServiceInterface.addProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('STOREKEEPER')")
    public ResponseEntity<Void> deleteProduct(@RequestBody Product product) {
        productServiceInterface.deleteProduct(product);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @PreAuthorize("hasRole('STOREKEEPER')")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product) {
        Product updatedProduct = productServiceInterface.changeProduct(product);
        return ResponseEntity.ok(updatedProduct);
    }
}