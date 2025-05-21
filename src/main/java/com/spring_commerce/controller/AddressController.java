package com.spring_commerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping
    // Gets a list of addresses
    public ResponseEntity<List<AddressDTO>> getAddresses() {
        final List<AddressDTO> addressesDTO =
                this.addressService.getAddresses();
        return ResponseEntity.ok(addressesDTO);
    }

    @PostMapping
    // Creates an address for the logged-in user
    public ResponseEntity<AddressDTO> createAddress(
            @RequestBody @Valid final AddressDTO addressDTO) {

        final User user = this.authUtil.loggedInUser();

        final AddressDTO createdAddressDTO =
                this.addressService.createAddress(addressDTO, user);

        return new ResponseEntity<>(createdAddressDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{addressId}")
    // Gets address by ID
    public ResponseEntity<AddressDTO> getAddress(
            @PathVariable final Long addressId) {
        final AddressDTO addressDTO = this.addressService.getAddress(addressId);
        return ResponseEntity.ok(addressDTO);
    }

    @PutMapping("/{addressId}")
    // Updates address by ID
    public ResponseEntity<AddressDTO> updateAddress(
            @PathVariable final Long addressId,
            @Valid @RequestBody final AddressDTO addressDTO) {
        final AddressDTO updatedAddressDTO =
                this.addressService.updateAddress(addressId, addressDTO);
        return new ResponseEntity<>(updatedAddressDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{addressId}")
    // Deletes address by ID
    public ResponseEntity<Void> deleteAddress(
            @PathVariable final Long addressId) {
        this.addressService.deleteAddress(addressId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/me")
    // Gets addresses of the logged-in user
    public ResponseEntity<List<AddressDTO>> getLoggedInUserAddresses() {
        final List<AddressDTO> addressesDTO =
                this.addressService.getLoggedInUserAddresses();
        return ResponseEntity.ok(addressesDTO);
    }


}
