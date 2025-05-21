package com.spring_commerce.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.spring_commerce.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUserEmail(String email);

    @Query("SELECT c FROM Cart c JOIN FETCH c.items ci JOIN FETCH ci.product p WHERE p.id = ?1")
    List<Cart> findAllByProductId(Long productId);

}
