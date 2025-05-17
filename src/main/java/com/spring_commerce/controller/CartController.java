package com.spring_commerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring_commerce.payload.CartDTO;
import com.spring_commerce.service.CartService;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    // Adds a specified quantity of a product to the cart
    @PostMapping("/carts/products/{productId}/quantity/{count}")
    public ResponseEntity<CartDTO> addProductToCart(
            @PathVariable Long productId,
            @PathVariable Integer count) {
        CartDTO cartDTO = cartService.addProductToCart(productId, count);
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }
}
