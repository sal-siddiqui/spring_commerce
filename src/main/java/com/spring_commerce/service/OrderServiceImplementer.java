package com.spring_commerce.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring_commerce.exceptions.APIException;
import com.spring_commerce.exceptions.ResourceNotFoundException;
import com.spring_commerce.model.Address;
import com.spring_commerce.model.Cart;
import com.spring_commerce.model.CartItem;
import com.spring_commerce.model.Order;
import com.spring_commerce.model.OrderItem;
import com.spring_commerce.model.Payment;
import com.spring_commerce.model.Product;
import com.spring_commerce.payload.OrderDTO;
import com.spring_commerce.payload.OrderItemDTO;
import com.spring_commerce.repositories.AddressRepository;
import com.spring_commerce.repositories.CartRepository;
import com.spring_commerce.repositories.OrderItemRepository;
import com.spring_commerce.repositories.OrderRepository;
import com.spring_commerce.repositories.PaymentRepository;
import com.spring_commerce.repositories.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderServiceImplementer extends BaseServiceImplementer implements OrderService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartService cartService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PaymentRepository paymentRepository;

    @Override
    @Transactional
    public OrderDTO placeOrder(
            final String loggedInEmail, final Long addressId, final String paymentMethod,
            final String pgName, final String pgPaymentId, final String pgStatus,
            final String pgResponseMessage) {

        final Cart cart = this.cartRepository.findByUserEmail(loggedInEmail);
        if (cart == null) {
            throw new ResourceNotFoundException("", "Cart", "Email", loggedInEmail);
        }

        final Address address = this.getOrThrow(this.addressRepository, addressId, "Address");

        final Order order = new Order();
        order.setEmail(loggedInEmail);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted!");
        order.setAddress(address);

        Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);
        payment.setOrder(order);

        payment = this.paymentRepository.save(payment);

        order.setPayment(payment);

        final Order savedOrder = this.orderRepository.save(order);

        final List<CartItem> cartItems = cart.getItems();
        if (cartItems.isEmpty()) {
            throw new APIException("Cart is empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (final CartItem item : cartItems) {
            orderItems.add(new OrderItem(
                    item.getProduct(),
                    savedOrder,
                    item.getQuantity(),
                    item.getDiscount(),
                    item.getProductPrice()));
        }

        orderItems = this.orderItemRepository.saveAll(orderItems);

        // Update product stock and remove items from cart
        cart.getItems().forEach(item -> {
            final Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - item.getQuantity());
            this.productRepository.save(product);

            this.cartService.deleteProductFromCart(cart.getId(), product.getId());
        });

        final OrderDTO orderDTO = this.modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(item -> orderDTO.getItemDTOs().add(this.modelMapper.map(item, OrderItemDTO.class)));

        return orderDTO;
    }

}
