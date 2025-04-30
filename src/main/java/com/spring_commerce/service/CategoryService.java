package com.spring_commerce.service;

import java.util.List;

import com.spring_commerce.model.Category;

public interface CategoryService {
    List<Category> getAllCategories();

    String createCategory(Category category);

    String deleteCategory(Long categoryId);

    String updateCategory(Category category, Long categoryId);
}
