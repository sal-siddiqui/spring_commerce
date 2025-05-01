package com.spring_commerce.service;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByandOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByandOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<Category> categories = categoryPage.getContent();

        if (categories.isEmpty())
            throw new APIException("No category created till now.");

        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOs);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalpages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO newCategoryDTO) {
        Category newCategory = modelMapper.map(newCategoryDTO, Category.class);
        Category existingCategory = categoryRepository.findByCategoryName(newCategory.getCategoryName());

        if (existingCategory != null) {
            throw new APIException("Category with the name " + newCategory.getCategoryName() + " already exists.");
        }

        Category createdCategory = categoryRepository.save(newCategory);
        return modelMapper.map(createdCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);

        if (optionalCategory.isEmpty()) {
            throw new ResourceNotFoundException("Category", "categoryId", categoryId);
        }

        Category existingCategory = optionalCategory.get();
        categoryRepository.delete(existingCategory);
        return modelMapper.map(existingCategory, CategoryDTO.class);

    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO updatedCategoryDTO, Long categoryId) {
        Category updatedCategory = modelMapper.map(updatedCategoryDTO, Category.class);
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);

        if (optionalCategory.isEmpty()) {
            throw new ResourceNotFoundException("Category", "categoryId", categoryId);
        }

        Category existingCategory = optionalCategory.get();
        existingCategory.setCategoryId(categoryId);
        existingCategory.setCategoryName(updatedCategory.getCategoryName());

        Category savedCategory = categoryRepository.save(existingCategory);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

}
