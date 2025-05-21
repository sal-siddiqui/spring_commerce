package com.spring_commerce.service;

import java.util.List;

import com.spring_commerce.model.User;
import com.spring_commerce.payload.AddressDTO;

public interface AddressService {

    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAddresses();

}
