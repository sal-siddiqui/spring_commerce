package com.spring_commerce.service;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.spring_commerce.exceptions.APIException;
import com.spring_commerce.exceptions.ResourceNotFoundException;

public abstract class BaseServiceImplementer {
    public <T> Boolean checkResourceExists(JpaRepository<T, Long> repository, Long id) {
        return repository.existsById(id);
    }

    public <T> T getOrThrow(JpaRepository<T, Long> repository, Long id, String resourceName) {
        Optional<T> optionalResource = repository.findById(id);

        if (optionalResource.isEmpty()) {
            throw new ResourceNotFoundException(resourceName, "ID", id);
        }

        T resource = optionalResource.get();
        return resource;
    }

    public <T> Pageable getPageable(Class<T> entityClass, Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder) {

        if (pageNumber < 0) {
            throw new APIException("Page number must be a non-negative integer.");
        }

        if (pageSize <= 0) {
            throw new APIException("Page size must be a positive integer.");
        }

        if (!sortOrder.equalsIgnoreCase("asc") && !sortOrder.equalsIgnoreCase("desc")) {
            throw new APIException("Sort order must be 'asc' or 'desc'.");
        }

        boolean validSortField = Arrays.stream(entityClass.getDeclaredFields())
                .anyMatch(field -> field.getName().equals(sortBy));

        if (!validSortField) {
            throw new APIException("Invalid sort field: '" + sortBy + "'");
        }

        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        return PageRequest.of(pageNumber, pageSize, sort);

    }

    public <T> Page<T> getPaginatedResponse(
            JpaRepository<T, Long> repository,
            Class<T> entityClass,
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder) {

        Pageable pageable = getPageable(entityClass, pageNumber, pageSize, sortBy, sortOrder);
        Page<T> page = repository.findAll(pageable);

        if (page.getTotalPages() == 0) {
            throw new APIException("No resources found.");
        }

        if (pageNumber >= page.getTotalPages()) {
            throw new APIException("Requested page number exceeds total available pages.");
        }

        return page;
    }

}
