package com.spring_commerce.service;

import java.util.Arrays;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.spring_commerce.exceptions.APIException;
import com.spring_commerce.exceptions.ResourceNotFoundException;

public abstract class BaseServiceImplementer {
    // Checks if a resource with the given ID exists in the repository
    public <T> Boolean checkResourceExists(
            final JpaRepository<T, Long> repository, final Long id) {
        return repository.existsById(id);
    }


    // Retrieve a resource by ID or throw if not found
    public <T> T getOrThrow(final JpaRepository<T, Long> repository,
            final Long id, final String resourceName) {
        return repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(resourceName, "ID", id));
    }

    // Delete a resource by ID or throw if not found
    public <T> void deleteOrThrow(final JpaRepository<T, Long> repository,
            final Long id, final String resourceName) {
        this.getOrThrow(repository, id, resourceName); // Ensure resource exists
        repository.deleteById(id); // Proceed with deletion
    }


    // Builds a Pageable object with validation for page, size, sort field, and
    // order
    public <T> Pageable getPageable(final Class<T> entityClass,
            final Integer pageNumber, final Integer pageSize,
            final String sortBy, final String sortOrder) {

        if (pageNumber < 0) {
            throw new APIException(
                    "Page number must be a non-negative integer.");
        }

        if (!"asc".equalsIgnoreCase(sortOrder)
                && !"desc".equalsIgnoreCase(sortOrder)) {
            throw new APIException("Sort order must be 'asc' or 'desc'.");
        }

        final boolean validSortField =
                Arrays.stream(entityClass.getDeclaredFields())
                        .anyMatch(field -> field.getName().equals(sortBy));

        if (!validSortField) {
            throw new APIException("Invalid sort field: '" + sortBy + "'");
        }

        final Sort sort =
                "asc".equalsIgnoreCase(sortOrder) ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending();

        return PageRequest.of(pageNumber, pageSize, sort);
    }


    // Retrieves a paginated and sorted page of entities or throws an exception if
    // invalid
    public <T> Page<T> getPaginatedResponse(
            final JpaRepository<T, Long> repository, final Class<T> entityClass,
            final Integer pageNumber, final Integer pageSize,
            final String sortBy, final String sortOrder) {

        final Pageable pageable = this.getPageable(entityClass, pageNumber,
                pageSize, sortBy, sortOrder);
        final Page<T> page = repository.findAll(pageable);

        if (page.getTotalPages() == 0) {
            throw new APIException("No resources found.");
        }

        if (pageNumber >= page.getTotalPages()) {
            throw new APIException(
                    "Requested page number exceeds total available pages.");
        }

        return page;
    }
}
