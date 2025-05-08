package com.spring_commerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    // Auto-generated primary key
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Required fields from request body
    private String name;
    private String description;
    private Integer quantity;
    private Double price;
    private Double discount;

    // Calculated fields
    private Double specialPrice;
    private String image;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
