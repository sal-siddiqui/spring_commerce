package com.spring_commerce.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.spring_commerce.model.User;
import com.spring_commerce.repositories.UserRepository;

@Component
public class AuthUtils {
    @Autowired
    UserRepository userRepository;

    // Returns email of the currently authenticated user
    public String loggedInEmail() {
        return this.getUser().getEmail();
    }

    // Returns ID of the currently authenticated user
    public Long loggedInUserId() {
        return this.getUser().getId();
    }

    // Returns the currently authenticated user
    public User loggedInUser() {
        return this.getUser();
    }

    // Retrieves the currently authenticated user
    private User getUser() {
        final Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        return this.userRepository.findByUsername(auth.getName()).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));
    }

}
