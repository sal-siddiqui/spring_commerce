package com.spring_commerce.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring_commerce.exceptions.ResourceNotFoundException;
import com.spring_commerce.model.Category;
import com.spring_commerce.model.Product;
import com.spring_commerce.payload.ProductDTO;
import com.spring_commerce.repositories.CategoryRepository;
import com.spring_commerce.repositories.ProductRepository;

@Service
public class ProductServiceImplementer implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProductDTO createProduct(ProductDTO newProductDTO, Long categoryId) {
        Optional<Category> existingCategory = categoryRepository.findById(categoryId);

        if (existingCategory.isEmpty())
            throw new ResourceNotFoundException("Category", "categoryId", categoryId);

        Product newProduct = modelMapper.map(newProductDTO, Product.class);

        newProduct.setCategory(existingCategory.get());

        double specialPrice = newProductDTO.getPrice() * (100 - newProductDTO.getDiscount()) / 100;
        newProduct.setSpecialPrice(specialPrice);

        newProduct.setImage("default.png");

        Product savedProduct = productRepository.save(newProduct);
        ProductDTO savedProductDTO = modelMapper.map(savedProduct, ProductDTO.class);

        return savedProductDTO;
    }

}
