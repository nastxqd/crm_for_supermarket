package com.shop.shop.buiseness;

import com.shop.shop.data.dtos.ProductDto;
import com.shop.shop.data.entities.Product;
import com.shop.shop.data.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProductService implements ProductServiceInterface{
    private ProductRepository productRepository;
    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }
    @Override
    public Product getById(Long id) {
        return productRepository.getProductById(id);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Product product) {
        productRepository.delete(product);
    }

    @Override
    public Product changeProduct(Product product) {
        return null;
    }

    @Override
    public Product moveFromCatalog(Product product) {
        return null;
    }
}
