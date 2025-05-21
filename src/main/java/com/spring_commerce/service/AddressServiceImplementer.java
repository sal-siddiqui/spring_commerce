package com.spring_commerce.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring_commerce.model.Address;
import com.spring_commerce.model.User;
import com.spring_commerce.payload.AddressDTO;
import com.spring_commerce.repositories.AddressRepository;
import com.spring_commerce.utils.AuthUtils;

@Service
public class AddressServiceImplementer extends BaseServiceImplementer
        implements AddressService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    AuthUtils authUtils;

    @Override
    public AddressDTO createAddress(final AddressDTO addressDTO,
            final User user) {
        // Map DTO to entity
        final Address address = this.modelMapper.map(addressDTO, Address.class);

        // // Add new address to user's address list
        // final List<Address> addresses = user.getAddresses();
        // addresses.add(address);
        // user.setAddresses(addresses);

        // Associate user with the new address
        address.setUser(user);

        // Persist the address entity
        final Address savedAddress = this.addressRepository.save(address);

        // Map saved entity back to DTO and return
        return this.modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        // Fetch all Address entities from the repository
        final List<Address> addresses = this.addressRepository.findAll();

        // Convert entities to DTOs using ModelMapper
        return addresses.stream()
                .map(address -> this.modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO getAddress(final Long addressId) {
        // Fetch address or throw if not found
        final Address address =
                this.getOrThrow(this.addressRepository, addressId, "Address");

        // Map entity to DTO
        return this.modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getLoggedInUserAddresses() {
        // Get the currently logged-in user
        final User user = this.authUtils.loggedInUser();

        // Fetch all addresses associated with the user
        final List<Address> addresses = user.getAddresses();

        // Map each address entity to a DTO
        return addresses.stream()
                .map(address -> this.modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO updateAddress(final Long addressId,
            final AddressDTO addressDTO) {
        // Retrieve existing address or throw if not found
        final Address addressFromDB =
                this.getOrThrow(this.addressRepository, addressId, "Address");

        // Map incoming DTO to Address entity
        final Address newAddress =
                this.modelMapper.map(addressDTO, Address.class);

        // Copy newAddress fields into the existing entity to update it
        this.modelMapper.map(newAddress, addressFromDB);

        // Persist the updated address
        this.addressRepository.save(addressFromDB);

        // Map updated entity back to DTO and return
        return this.modelMapper.map(addressFromDB, AddressDTO.class);
    }

    @Override
    public void deleteAddress(final Long addressId) {
        // Delete address by ID or throw if not found
        this.deleteOrThrow(this.addressRepository, addressId, "Address");
    }


}
