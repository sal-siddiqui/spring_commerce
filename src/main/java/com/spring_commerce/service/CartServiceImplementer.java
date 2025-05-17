package com.spring_commerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring_commerce.model.Cart;
import com.spring_commerce.model.Product;
import com.spring_commerce.payload.CartDTO;
import com.spring_commerce.repositories.CartRepository;
import com.spring_commerce.repositories.ProductRepository;

@Service
public class CartServiceImplementer extends BaseServiceImplementer implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AuthUtil authUtil;

    @Override
    public CartDTO addProductToCart(Long productId, Integer count) {
        /*
         * 1. Retrieve existing cart or create a new one
         * 2. Retrieve product details
         * 3. Create cart item
         * 4. Save cart item
         * 5. Return cart item
         */
        // 1
        Cart cart = getOrCreateCart();

        // 2
        Product product = getOrThrow(productRepository, productId, "Product");

        //

        return null;
    }

    // Retrieves the user's cart or creates a new one if it doesn't exist
    private Cart getOrCreateCart() {
        String userEmail = authUtil.loggedInUser().getEmail();
        Cart cart = cartRepository.findByUserEmail(userEmail);

        if (cart != null) {
            return cart;
        }

        Cart newCart = new Cart();
        newCart.setTotalPrice(0.00);
        newCart.setUser(authUtil.loggedInUser());

        return cartRepository.save(newCart);
    }

}
