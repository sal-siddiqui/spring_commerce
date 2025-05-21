package com.spring_commerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring_commerce.payload.OrderDTO;
import com.spring_commerce.payload.OrderRequestDTO;
import com.spring_commerce.service.OrderService;
import com.spring_commerce.utils.AuthUtils;

@RestController
@RequestMapping("/api/orders/")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthUtils authUtil;

    @PostMapping("/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> placeOrder(
            @PathVariable final String paymentMethod,
            @RequestBody final OrderRequestDTO orderRequestDTO) {

        // Place the order using provided payment and order details
        final OrderDTO order = this.orderService.placeOrder(
                this.authUtil.loggedInEmail(),
                orderRequestDTO.getAddressId(),
                paymentMethod,
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage());

        // Return the created order with HTTP 201 status
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

}
