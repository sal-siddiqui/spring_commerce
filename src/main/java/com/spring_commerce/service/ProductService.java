package com.spring_commerce.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.spring_commerce.payload.ProductDTO;
import com.spring_commerce.payload.ProductResponse;

public interface ProductService {

    ProductDTO createProduct(ProductDTO newProductDTO, Long categoryId);

    ProductResponse getAllProducts();

    ProductResponse getProductsbyCategoryId(Long categoryId);

    ProductResponse getProductsbyKeyword(String keyword);

    ProductDTO updateProduct(ProductDTO newProductDTO, Long productId);

    void deleteProduct(Long productId);

    ProductDTO updateProductImage(Long productId, MultipartFile imageFile) throws IOException;

}
