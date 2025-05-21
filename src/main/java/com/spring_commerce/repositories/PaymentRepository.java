package com.spring_commerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring_commerce.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
