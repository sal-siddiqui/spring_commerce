package com.spring_commerce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.spring_commerce.model.Category;

@Service
public class CategoryServiceImplementer implements CategoryService {

    private List<Category> categories = new ArrayList<>();
    private Long nextId = 1L;

    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public String createCategory(Category category) {
        category.setCategoryId(nextId++);
        categories.add(category);
        return String.format("%s was added successfully.", category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Optional<Category> categoryIsPresent = categories.stream().filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst();

        if (categoryIsPresent.isPresent()) {
            Category category = categoryIsPresent.get();
            categories.remove(category);
            return String.format("%s was deleted.", category);
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category was not found.");

    }

    @Override
    public String updateCategory(Category newCategory, Long categoryId) {
        Optional<Category> categoryIsPresent = categories.stream().filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst();

        if (categoryIsPresent.isPresent()) {
            Category currentCategory = categoryIsPresent.get();
            currentCategory.setCategoryId(categoryId);
            currentCategory.setCategoryName(newCategory.getCategoryName());
            return String.format("Category [id = %d] was updated.", categoryId);
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found.");

    }

}
