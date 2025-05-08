package com.spring_commerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spring_commerce.payload.ProductDTO;
import com.spring_commerce.payload.ProductResponse;
import com.spring_commerce.service.ProductService;

import jakarta.validation.Valid;

@RestController
public class ProductController {

  @Autowired
  private ProductService productService;

  // Create Product
  @PostMapping("/api/admin/categories/{categoryId}/product")
  public ResponseEntity<ProductDTO> createProduct(
      @Valid @RequestBody ProductDTO newProductDTO, @PathVariable Long categoryId) {
    ProductDTO createdProductDTO = productService.createProduct(newProductDTO, categoryId);
    return new ResponseEntity<>(createdProductDTO, HttpStatus.CREATED);
  }

  // Get All Products
  @GetMapping("/api/public/products")
  public ResponseEntity<ProductResponse> getAllProducts() {
    ProductResponse productResponse = productService.getAllProducts();
    return new ResponseEntity<>(productResponse, HttpStatus.OK);
  }

  // Get Products by Category Id
  @GetMapping("/api/public/categories/{categoryId}/products")
  public ResponseEntity<ProductResponse> getProductsbyCategoryId(@PathVariable Long categoryId) {
    ProductResponse productResponse = productService.getProductsbyCategoryId(categoryId);
    return new ResponseEntity<>(productResponse, HttpStatus.OK);
  }

  // Get Products by Keyword
  @GetMapping("/api/public/products/keyword/{keyword}")
  public ResponseEntity<ProductResponse> getProductsbyKeyword(@PathVariable String keyword) {
    ProductResponse productResponse = productService.getProductsbyKeyword(keyword);
    return new ResponseEntity<>(productResponse, HttpStatus.OK);
  }

  // Update Product
  @PutMapping("/api/admin/products/{productId}")
  public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO newProductDTO,
      @PathVariable Long productId) {
    ProductDTO updatedProductDTO = productService.updateProduct(newProductDTO, productId);
    return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
  }

}
