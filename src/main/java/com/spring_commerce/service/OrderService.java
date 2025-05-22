package com.spring_commerce.service;

import com.spring_commerce.payload.OrderDTO;

import jakarta.transaction.Transactional;

public interface OrderService {

    @Transactional
    OrderDTO placeOrder(String loggedInEmail, Long addressId, String paymentMethod, String pgName, String pgPaymentId,
            String pgStatus, String pgResponseMessage);

}
