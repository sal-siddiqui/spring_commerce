package com.spring_commerce.payload;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private String email;
    private List<OrderItemDTO> itemDTOs = new ArrayList<>();
    private LocalDate orderDate;
    private PaymentDTO paymentDTO;
    private Double totalAmount;
    private String orderStatus;
    private Long addressId;
}
