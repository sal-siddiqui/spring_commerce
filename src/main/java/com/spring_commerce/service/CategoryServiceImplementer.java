package com.spring_commerce.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public CategoryResponse getAllCategories(final Integer pageNumber, final Integer pageSize, final String sortBy,
            final String sortOrder) {

        final Page<Category> categoryPage = this.getPaginatedResponse(
                this.categoryRepository,
                Category.class,
                pageNumber,
                pageSize,
                sortBy,
                sortOrder);

        final List<CategoryDTO> categoryDTOs = categoryPage.getContent().stream()
                .map(category -> this.modelMapper.map(category, CategoryDTO.class))
                .toList();

        final CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOs);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalpages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(final CategoryDTO newCategoryDTO) {

        if (this.categoryRepository.existsByName(newCategoryDTO.getName())) {
            throw new APIException("Category with the name " + newCategoryDTO.getName() + " already exists.");
        }

        final Category newCategory = this.modelMapper.map(newCategoryDTO, Category.class);

        final Category createdCategory = this.categoryRepository.save(newCategory);
        return this.modelMapper.map(createdCategory, CategoryDTO.class);
    }

    @Override
    public void deleteCategory(final Long categoryId) {
        final Category existingCategory = this.getOrThrow(this.categoryRepository, categoryId, "Category");

        this.categoryRepository.delete(existingCategory);
    }

    @Override
    public CategoryDTO updateCategory(final CategoryDTO updatedCategoryDTO, final Long categoryId) {
        final Category existingCategory = this.getOrThrow(this.categoryRepository, categoryId, "Category");

        this.modelMapper.map(updatedCategoryDTO, existingCategory);

        final Category savedCategory = this.categoryRepository.save(existingCategory);
        return this.modelMapper.map(savedCategory, CategoryDTO.class);
    }

}
