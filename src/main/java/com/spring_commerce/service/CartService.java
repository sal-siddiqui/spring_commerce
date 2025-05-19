package com.spring_commerce.service;

import java.util.List;

import com.spring_commerce.payload.CartDTO;

public interface CartService {

    CartDTO addProductToCart(Long productId, Integer count);

    List<CartDTO> getCarts();

    CartDTO getCart();

    CartDTO updateCartProduct(Long productId, int delete);

    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductInCarts(Long id, Long productId);

}
