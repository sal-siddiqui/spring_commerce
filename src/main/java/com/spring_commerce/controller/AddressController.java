package com.spring_commerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring_commerce.model.User;
import com.spring_commerce.payload.AddressDTO;
import com.spring_commerce.service.AddressService;
import com.spring_commerce.utils.AuthUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/addresses/")
public class AddressController {

    @Autowired
    AddressService addressService;

    @Autowired
    AuthUtils authUtil;

    // Creates a new address for the logged-in user
    public ResponseEntity<AddressDTO> createAddress(@RequestBody @Valid final AddressDTO addressDTO) {
        // Get current authenticated user
        final User user = this.authUtil.loggedInUser();

        // Create address linked to the user
        final AddressDTO createdAddressDTO = this.addressService.createAddress(addressDTO, user);

        // Return created address with 201 status
        return new ResponseEntity<>(createdAddressDTO, HttpStatus.CREATED);
    }

}
