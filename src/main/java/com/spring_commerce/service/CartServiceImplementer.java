package com.spring_commerce.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring_commerce.exceptions.APIException;
import com.spring_commerce.model.Cart;
import com.spring_commerce.model.CartItem;
import com.spring_commerce.model.Product;
import com.spring_commerce.payload.CartDTO;
import com.spring_commerce.payload.ProductDTO;
import com.spring_commerce.repositories.CartItemRepository;
import com.spring_commerce.repositories.CartRepository;
import com.spring_commerce.repositories.ProductRepository;
import com.spring_commerce.utils.AuthUtils;

@Service
public class CartServiceImplementer extends BaseServiceImplementer implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthUtils authUtil;

    /**
     * Adds a product to the cart with the specified quantity and returns the
     * updated cart DTO.
     */
    @Override
    public CartDTO addProductToCart(Long productId, Integer count) {
        // Retrieve existing cart or create a new one
        Cart cart = getOrCreateCart();

        // Retrieve product details or throw exception if not found
        Product product = getOrThrow(productRepository, productId, "Product");

        // Check if product already exists in cart
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (cartItem != null) {
            throw new APIException(String.format("Product '%s' already exists in cart.", product.getName()));
        }

        // Validate product stock quantity
        if (product.getQuantity() == 0) {
            throw new APIException(String.format("Product '%s' out of stock.", product.getName()));
        }
        if (product.getQuantity() < count) {
            throw new APIException("Requested quantity exceeds available stock.");
        }

        // Create and save new cart item
        cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setCart(cart);
        cartItem.setQuantity(count);
        cartItem.setDiscount(product.getDiscount());
        cartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(cartItem);

        // Update cart total price
        cart.setTotalPrice(cart.getTotalPrice() + product.getSpecialPrice() * count);
        cart.getItems().add(cartItem);

        cartRepository.save(cart);

        // Map cart entity to DTO and include product details with quantities
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getItems();

        cartDTO.setProducts(
                cartItems.stream()
                        .map(item -> {
                            ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                            productDTO.setQuantity(item.getQuantity());
                            return productDTO;
                        })
                        .toList());

        return cartDTO;
    }

    /**
     * Retrieves the logged-in user's cart, or creates a new one if none exists.
     */
    private Cart getOrCreateCart() {
        String userEmail = authUtil.loggedInUser().getEmail();

        // Attempt to find an existing cart by user email
        Cart cart = cartRepository.findByUserEmail(userEmail);
        if (cart != null) {
            return cart; // Existing cart found
        }

        // No cart found — create and initialize a new one
        Cart newCart = new Cart();
        newCart.setTotalPrice(0.00);
        newCart.setUser(authUtil.loggedInUser());

        // Save and return the newly created cart
        return cartRepository.save(newCart);
    }

    /**
     * Retrieves all carts from the repository, maps them to CartDTOs,
     * and includes mapped ProductDTOs for each cart item.
     */
    @Override
    public List<CartDTO> getCarts() {
        List<Cart> carts = cartRepository.findAll();

        if (carts.isEmpty()) {
            throw new APIException("No carts available");
        }

        return carts.stream()
                .map(cart -> {
                    // Map Cart to CartDTO
                    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

                    // Map each product in cart items to ProductDTO
                    List<ProductDTO> productDTOs = cart.getItems().stream()
                            .map(item -> modelMapper.map(item.getProduct(), ProductDTO.class))
                            .collect(Collectors.toList());

                    cartDTO.setProducts(productDTOs);
                    return cartDTO;
                })
                .collect(Collectors.toList());
    }

}
