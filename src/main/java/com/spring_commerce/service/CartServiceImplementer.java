package com.spring_commerce.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

import jakarta.transaction.Transactional;

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
     * Adds a product to the cart and returns the updated cart.
     */
    @Override
    public CartDTO addProductToCart(final Long productId, final Integer count) {
        // Retrieve existing cart or create a new one
        final Cart cart = this.getOrCreateCart();

        // Retrieve product details or throw exception if not found
        final Product product = this.getOrThrow(this.productRepository, productId, "Product");

        // Check if product already exists in cart
        CartItem cartItem = this.cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (cartItem != null) {
            throw new APIException(
                    String.format("Product '%s' already exists in cart.", product.getName()));
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

        this.cartItemRepository.save(cartItem);

        // Update cart total price
        cart.setTotalPrice(cart.getTotalPrice() + product.getSpecialPrice() * count);
        cart.getItems().add(cartItem);

        this.cartRepository.save(cart);

        // Map cart entity to DTO and include product details with quantities
        final CartDTO cartDTO = this.modelMapper.map(cart, CartDTO.class);
        final List<CartItem> cartItems = cart.getItems();

        cartDTO.setProductDTOs(cartItems.stream().map(item -> {
            final ProductDTO productDTO = this.modelMapper.map(item.getProduct(), ProductDTO.class);
            productDTO.setQuantity(item.getQuantity());
            return productDTO;
        }).toList());

        return cartDTO;
    }

    /**
     * Retrieves the logged-in user's cart, or creates a new one if none exists.
     */
    private Cart getOrCreateCart() {
        final String userEmail = this.authUtil.loggedInUser().getEmail();

        // Attempt to find an existing cart by user email
        final Cart cart = this.cartRepository.findByUserEmail(userEmail);
        if (cart != null) {
            return cart; // Existing cart found
        }

        // No cart found — create and initialize a new one
        final Cart newCart = new Cart();
        newCart.setTotalPrice(0.00);
        newCart.setUser(this.authUtil.loggedInUser());

        // Save and return the newly created cart
        return this.cartRepository.save(newCart);
    }

    /**
     * Retrieves all carts from the repository.
     */
    @Override
    public List<CartDTO> getCarts() {
        final List<Cart> carts = this.cartRepository.findAll();

        if (carts.isEmpty()) {
            throw new APIException("No carts available");
        }

        return carts.stream().map(cart -> {
            // Map Cart to CartDTO
            final CartDTO cartDTO = this.modelMapper.map(cart, CartDTO.class);

            // Map each product in cart items to ProductDTO
            final List<ProductDTO> productDTOs = cart.getItems().stream().map(item -> {
                final ProductDTO productDTO = this.modelMapper.map(item.getProduct(), ProductDTO.class);
                productDTO.setQuantity(item.getQuantity());
                return productDTO;
            }).collect(Collectors.toList());

            cartDTO.setProductDTOs(productDTOs);
            return cartDTO;
        }).collect(Collectors.toList());
    }

    /**
     * Retrieves the current user's shopping cart.
     */
    @Override
    public CartDTO getCart() {
        // Retrieve the logged-in user's email
        final String userEmail = this.authUtil.loggedInEmail();

        // Fetch the user's cart or throw if not found
        final Cart cart = this.cartRepository.findByUserEmail(userEmail);
        if (cart == null) {
            throw new APIException("Cart not found for user: " + userEmail);
        }

        // Map cart to DTO
        final CartDTO cartDTO = this.modelMapper.map(cart, CartDTO.class);

        // Map each cart item to a ProductDTO with quantity
        cartDTO.setProductDTOs(cart.getItems().stream().map(item -> {
            final ProductDTO productDTO = this.modelMapper.map(item.getProduct(), ProductDTO.class);
            productDTO.setQuantity(item.getQuantity());
            return productDTO;
        }).toList());

        return cartDTO;
    }

    /**
     * Updates the current user's shopping cart.
     */
    @Override
    @Transactional
    public CartDTO updateCartProduct(final Long productId, final int quantityChange) {
        // Get the current user's cart ID
        final Long cartId = this.authUtil.loggedInUser().getCart().getId();

        // Fetch the cart and product, or throw an exception if not found
        final Cart cart = this.getOrThrow(this.cartRepository, cartId, "Cart");
        final Product product = this.getOrThrow(this.productRepository, productId, "Product");

        // Find the cart item for the given product
        final CartItem cartItem = this.cartItemRepository.findByCartIdAndProductId(cartId, productId);

        if (cartItem == null) {
            throw new APIException("Cart item not found.");
        }

        final int newQuantity = cartItem.getQuantity() + quantityChange;

        if (newQuantity < 0) {
            throw new APIException("...");
        }

        if (newQuantity == 0) {
            this.deleteProductFromCart(cartId, productId);
        } else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantityChange);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + cartItem.getProductPrice() * quantityChange);

            this.cartRepository.save(cart);
        }

        final CartItem updatedItem = this.cartItemRepository.save(cartItem);

        if (updatedItem.getQuantity() == 0) {
            this.cartItemRepository.deleteById(updatedItem.getId());
        }

        final CartDTO cartDTO = this.modelMapper.map(cart, CartDTO.class);

        final List<CartItem> cartItems = cart.getItems();

        final Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            final ProductDTO prd = this.modelMapper.map(item.getProduct(), ProductDTO.class);
            prd.setQuantity(item.getQuantity());
            return prd;
        });

        cartDTO.setProductDTOs(productStream.toList());

        return cartDTO;

    }

    @Override
    @Transactional
    public String deleteProductFromCart(final Long cartId, final Long productId) {
        // Fetch the cart and product, or throw an exception if not found
        final Cart cart = this.getOrThrow(this.cartRepository, cartId, "Cart");
        final Product product = this.getOrThrow(this.productRepository, productId, "Product");

        // Find the cart item for the given product
        final CartItem cartItem = this.cartItemRepository.findByCartIdAndProductId(cartId, productId);

        if (cartItem == null) {
            throw new APIException("Cart item not found.");
        }

        cart.setTotalPrice(
                cart.getTotalPrice() - cartItem.getProductPrice() * cartItem.getQuantity());

        this.cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        return String.format("Product '%s' removed from cart.", product.getName());

    }

    @Override
    public void updateProductInCarts(final Long id, final Long productId) {
        final Cart cart = this.getOrThrow(this.cartRepository, id, "Cart");
        final Product product = this.getOrThrow(this.productRepository, productId, "Product");

        CartItem cartItem = this.cartItemRepository.findByCartIdAndProductId(id, productId);

        if (cartItem == null) {
            throw new APIException("...");
        }

        final double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());

        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItem = this.cartItemRepository.save(cartItem);

    }

}
