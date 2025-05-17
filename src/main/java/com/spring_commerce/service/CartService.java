package com.spring_commerce.service;

import com.spring_commerce.payload.CartDTO;

public interface CartService {

    CartDTO addProductToCart(Long productId, Integer count);

}
