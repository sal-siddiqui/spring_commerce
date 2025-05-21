package com.spring_commerce.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring_commerce.model.Address;
import com.spring_commerce.model.User;
import com.spring_commerce.payload.AddressDTO;
import com.spring_commerce.repositories.AddressRepository;

@Service
public class AddressServiceImplementer implements AddressService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AddressRepository addressRepository;



    @Override
    public AddressDTO createAddress(final AddressDTO addressDTO, final User user) {
        // Map DTO to entity
        final Address address = this.modelMapper.map(addressDTO, Address.class);

        // Add new address to user's address list
        final List<Address> addresses = user.getAddresses();
        addresses.add(address);
        user.setAddresses(addresses);

        // Associate user with the new address
        address.setUser(user);

        // Persist the address entity
        final Address savedAddress = this.addressRepository.save(address);

        // Map saved entity back to DTO and return
        return this.modelMapper.map(savedAddress, AddressDTO.class);
    }


}
