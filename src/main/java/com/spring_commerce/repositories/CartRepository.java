package com.spring_commerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring_commerce.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUserEmail(String email);
}
