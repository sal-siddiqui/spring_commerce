package com.spring_commerce.service;

import com.spring_commerce.payload.ProductDTO;
import com.spring_commerce.payload.ProductResponse;

public interface ProductService {

    ProductDTO createProduct(ProductDTO newProductDTO, Long categoryId);

    ProductResponse getAllProducts();

    ProductResponse getProductsbyCategoryId(Long categoryId);

    ProductResponse getProductsbyKeyword(String keyword);

    ProductDTO updateProduct(ProductDTO newProductDTO, Long productId);

}
