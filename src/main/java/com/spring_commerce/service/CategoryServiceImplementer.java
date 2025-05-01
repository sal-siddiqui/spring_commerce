package com.spring_commerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.spring_commerce.model.Category;
import com.spring_commerce.repositories.CategoryRepository;

@Service
public class CategoryServiceImplementer implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public String createCategory(Category category) {
        categoryRepository.save(category);
        return String.format("%s was added successfully.", category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Optional<Category> categoryIsPresent = categoryRepository.findById(categoryId);

        if (categoryIsPresent.isPresent()) {
            Category category = categoryIsPresent.get();
            categoryRepository.delete(category);
            return String.format("%s was deleted.", category);
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found.");

    }

    @Override
    public String updateCategory(Category newCategory, Long categoryId) {
        Optional<Category> categoryIsPresent = categoryRepository.findById(categoryId);

        if (categoryIsPresent.isPresent()) {
            Category currentCategory = categoryIsPresent.get();
            currentCategory.setCategoryId(categoryId);
            currentCategory.setCategoryName(newCategory.getCategoryName());
            categoryRepository.save(currentCategory);
            return String.format("Category [id = %d] was updated.", categoryId);
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found.");

    }

}
