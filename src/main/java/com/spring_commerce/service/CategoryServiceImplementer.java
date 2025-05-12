package com.spring_commerce.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.spring_commerce.exceptions.APIException;
import com.spring_commerce.model.Category;
import com.spring_commerce.payload.CategoryDTO;
import com.spring_commerce.payload.CategoryResponse;
import com.spring_commerce.repositories.CategoryRepository;

@Service
public class CategoryServiceImplementer extends BaseServiceImplementer implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        List<CategoryDTO> categoryDTOs = categoryPage.getContent().stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        CategoryResponse response = new CategoryResponse();
        response.setContent(categoryDTOs);
        response.setPageNumber(categoryPage.getNumber());
        response.setPageSize(categoryPage.getSize());
        response.setTotalElements(categoryPage.getTotalElements());
        response.setTotalpages(categoryPage.getTotalPages());
        response.setLastPage(categoryPage.isLast());

        return response;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO newCategoryDTO) {

        if (categoryRepository.existsByName(newCategoryDTO.getName())) {
            throw new APIException("Category with the name " + newCategoryDTO.getName() + " already exists.");
        }

        Category newCategory = modelMapper.map(newCategoryDTO, Category.class);

        Category createdCategory = categoryRepository.save(newCategory);
        return modelMapper.map(createdCategory, CategoryDTO.class);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category existingCategory = getOrThrow(categoryRepository, categoryId, "Category");

        categoryRepository.delete(existingCategory);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO updatedCategoryDTO, Long categoryId) {
        Category existingCategory = getOrThrow(categoryRepository, categoryId, "Category");

        modelMapper.map(updatedCategoryDTO, existingCategory);

        Category savedCategory = categoryRepository.save(existingCategory);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

}
