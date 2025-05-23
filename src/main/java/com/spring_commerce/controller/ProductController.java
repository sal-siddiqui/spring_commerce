package com.spring_commerce.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spring_commerce.config.AppConstants;
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
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody final ProductDTO newProductDTO,
            @PathVariable final Long categoryId) {
        final ProductDTO createdProductDTO = this.productService.createProduct(newProductDTO, categoryId);
        return new ResponseEntity<>(createdProductDTO, HttpStatus.CREATED);
    }

    // Get All Products
    @GetMapping("/api/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) final Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) final Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) final String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) final String sortOrder) {
        final ProductResponse productResponse = this.productService.getAllProducts(pageNumber, pageSize, sortBy,
                sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    // Get Products by Category Id
    @GetMapping("/api/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsbyCategoryId(@PathVariable final Long categoryId,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) final Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) final Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) final String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) final String sortOrder) {

        final ProductResponse productResponse = this.productService.getProductsbyCategoryId(categoryId, pageNumber,
                pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);

    }

    // Get Products by Keyword
    @GetMapping("/api/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsbyKeyword(@PathVariable final String keyword,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) final Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) final Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) final String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) final String sortOrder) {

        final ProductResponse productResponse = this.productService.getProductsbyKeyword(keyword, pageNumber, pageSize,
                sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);

    }

    // Update Product
    @PutMapping("/api/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody final ProductDTO newProductDTO,
            @PathVariable final Long productId) {
        final ProductDTO updatedProductDTO = this.productService.updateProduct(newProductDTO, productId);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }

    // Delete Product
    @DeleteMapping("/api/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable final Long productId) {
        this.productService.deleteProduct(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Update Product Image
    @PutMapping("/api/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable final Long productId,
            @RequestParam("image") final MultipartFile imageFile) throws IOException {
        final ProductDTO updatedProductDTO = this.productService.updateProductImage(productId, imageFile);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }

}
