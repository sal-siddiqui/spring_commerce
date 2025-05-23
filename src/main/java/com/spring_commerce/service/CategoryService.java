package com.spring_commerce.service;

import com.spring_commerce.payload.CategoryDTO;
import com.spring_commerce.payload.CategoryResponse;

public interface CategoryService {
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    CategoryDTO createCategory(CategoryDTO newCategoryDTO);

    void deleteCategory(Long categoryId);

    CategoryDTO updateCategory(CategoryDTO updatedCategoryDTO, Long categoryId);
}
