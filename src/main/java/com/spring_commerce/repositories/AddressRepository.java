package com.spring_commerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring_commerce.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {


}
