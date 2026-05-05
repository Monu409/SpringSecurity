package com.example.security.SpringSecurity.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductModel {
    @Id
    private String id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String category;
}