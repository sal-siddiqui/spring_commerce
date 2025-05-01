package com.spring_commerce.service;

import com.spring_commerce.model.Category;
import com.spring_commerce.payload.CategoryResponse;

public interface CategoryService {
    CategoryResponse getAllCategories();

    String createCategory(Category category);

    String deleteCategory(Long categoryId);

    String updateCategory(Category category, Long categoryId);
}
