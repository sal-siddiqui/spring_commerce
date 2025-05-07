package com.spring_commerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Product {
    // -- Fields
    private Long productId;
    private String productName;
    private String description;
    private Integer quantity;
    private Double price;
    private double specialPrice;

    // -- Relationships
    @ManyToOne
    private Category category;
}
