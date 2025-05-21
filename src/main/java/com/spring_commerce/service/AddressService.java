package com.spring_commerce.service;

import com.spring_commerce.model.User;
import com.spring_commerce.payload.AddressDTO;

public interface AddressService {

    AddressDTO createAddress(AddressDTO addressDTO, User user);

}
