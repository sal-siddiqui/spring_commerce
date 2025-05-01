package com.spring_commerce.service;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring_commerce.exceptions.APIException;
import com.spring_commerce.exceptions.ResourceNotFoundException;
import com.spring_commerce.model.Category;
import com.spring_commerce.payload.CategoryDTO;
import com.spring_commerce.payload.CategoryResponse;
import com.spring_commerce.repositories.CategoryRepository;

@Service
public class CategoryServiceImplementer implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        if (categories.isEmpty())
            throw new APIException("No category created till now.");

        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        return new CategoryResponse(categoryDTOs);
    }

    @Override
    public String createCategory(Category category) {
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());

        if (savedCategory != null) {
            throw new APIException("Category with the name " + category.getCategoryName() + " already exists.");
        }

        categoryRepository.save(category);
        return String.format("%s was added successfully.", category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Optional<Category> categoryIsPresent = categoryRepository.findById(categoryId);

        if (categoryIsPresent.isEmpty()) {
            throw new ResourceNotFoundException("Category", "categoryId", categoryId);
        }

        Category category = categoryIsPresent.get();
        categoryRepository.delete(category);
        return String.format("%s was deleted.", category);

    }

    @Override
    public String updateCategory(Category newCategory, Long categoryId) {
        Optional<Category> categoryIsPresent = categoryRepository.findById(categoryId);

        if (categoryIsPresent.isEmpty()) {
            throw new ResourceNotFoundException("Category", "categoryId", categoryId);
        }

        Category currentCategory = categoryIsPresent.get();
        currentCategory.setCategoryId(categoryId);
        currentCategory.setCategoryName(newCategory.getCategoryName());
        categoryRepository.save(currentCategory);
        return String.format("Category [id = %d] was updated.", categoryId);

    }

}
