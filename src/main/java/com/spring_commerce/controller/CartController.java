package com.spring_commerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring_commerce.payload.CartDTO;
import com.spring_commerce.service.CartService;

@RestController
@RequestMapping("/api/carts/")
public class CartController {

    @Autowired
    private CartService cartService;

    // Adds a specified quantity of a product to the cart
    @PostMapping("/products/{productId}/quantity/{count}")
    public ResponseEntity<CartDTO> addProductToCart(
            @PathVariable final Long productId,
            @PathVariable final Integer count) {
        final CartDTO cartDTO =
                this.cartService.addProductToCart(productId, count);
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }

    // Retrieves the list of carts
    @GetMapping("")
    public ResponseEntity<List<CartDTO>> getCarts() {
        final List<CartDTO> cartDTOs = this.cartService.getCarts();
        return new ResponseEntity<>(cartDTOs, HttpStatus.OK);
    }

    // Fetches the user's cart
    @GetMapping("/users/cart")
    public ResponseEntity<CartDTO> getCart() {
        final CartDTO cartDTO = this.cartService.getCart();
        return ResponseEntity.ok(cartDTO);
    }

    // Updates the user's cart
    @PutMapping("/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(
            @PathVariable final Long productId,
            @PathVariable final String operation) {
        final CartDTO cartDTO = this.cartService.updateCartProduct(productId,
                "delete".equalsIgnoreCase(operation) ? -1 : 1);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    // Deletes a products from the user's cart
    @DeleteMapping("/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(
            @PathVariable final Long cartId,
            @PathVariable final Long productId) {
        final String status =
                this.cartService.deleteProductFromCart(cartId, productId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

}
