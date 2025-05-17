package com.spring_commerce.payload;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long id;
    private Double totalPrice = 0.0;
    private List<ProductDTO> producuts = new ArrayList<>();
}
