package com.spring_commerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring_commerce.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

}
