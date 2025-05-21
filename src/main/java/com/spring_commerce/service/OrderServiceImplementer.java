package com.spring_commerce.service;

import com.spring_commerce.payload.OrderDTO;

public class OrderServiceImplementer implements OrderService {

    @Override
    public OrderDTO placeOrder(
            final String loggedInEmail, final Long addressId, final String paymentMethod,
            final String pgName, final String pgPaymentId, final String pgStatus,
            final String pgResponseMessage) {

        return null;

    }

}
