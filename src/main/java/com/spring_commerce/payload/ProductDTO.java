package com.spring_commerce.payload;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    // Auto-generated primary key
    private Long productId;

    // Required fields from request body
    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String productName;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, length = 1000)
    private String description;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Discount is required")
    @Min(value = 0, message = "Discount cannot be less than 0")
    @Max(value = 100, message = "Discount cannot be more than 100")
    private Double discount;

    // Calculated fields
    private double specialPrice;
    private String image;
}
