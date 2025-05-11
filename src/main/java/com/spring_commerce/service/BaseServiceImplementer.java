package com.spring_commerce.service;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring_commerce.exceptions.ResourceNotFoundException;

public abstract class BaseServiceImplementer {
    public <T> Boolean resourceExists(JpaRepository<T, Long> repository, Long id) {
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
}
