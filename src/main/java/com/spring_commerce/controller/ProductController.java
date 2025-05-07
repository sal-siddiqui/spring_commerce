package com.spring_commerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spring_commerce.payload.ProductDTO;
import com.spring_commerce.service.ProductService;

import jakarta.validation.Valid;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/api/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO newProductDTO,
            @PathVariable Long categoryId) {
        ProductDTO createdProductDTO = productService.createProduct(newProductDTO, categoryId);
        return new ResponseEntity<>(createdProductDTO, HttpStatus.CREATED);
    }

}
