package com.spring_commerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long id;
    private ProductDTO product;
    private Integer quantity;
    private double discount;
    private double totalAmount;
}
