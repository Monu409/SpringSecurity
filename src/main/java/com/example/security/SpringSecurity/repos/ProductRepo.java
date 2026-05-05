package com.example.security.SpringSecurity.repos;

import com.example.security.SpringSecurity.models.ProductModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ProductRepo extends MongoRepository<ProductModel, String> {
    List<ProductModel> findByCategory(String category);
    List<ProductModel> findByNameContainingIgnoreCase(String name);
}