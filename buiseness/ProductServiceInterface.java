package com.shop.shop.buiseness;

import com.shop.shop.data.entities.Product;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ProductServiceInterface {
    public Product getById(Long id);
    public List<Product> getAllProducts();
    public Product addProduct(Product product);
    public void deleteProduct(Product product);
    public Product changeProduct(Product product);
    public Product moveFromCatalog(Product product);
}
