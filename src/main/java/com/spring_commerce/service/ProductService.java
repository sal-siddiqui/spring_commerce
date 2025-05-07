package com.spring_commerce.service;

import com.spring_commerce.payload.ProductDTO;

public interface ProductService {

    ProductDTO createProduct(ProductDTO productDTO, Long categoryId);

}
