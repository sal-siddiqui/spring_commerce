package com.spring_commerce.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring_commerce.model.AppRole;
import com.spring_commerce.model.Role;
import com.spring_commerce.model.User;
import com.spring_commerce.repositories.RoleRepository;
import com.spring_commerce.repositories.UserRepository;
import com.spring_commerce.security.jwt.JwtUtils;
import com.spring_commerce.security.request.LoginRequest;
import com.spring_commerce.security.request.SignupRequest;
import com.spring_commerce.security.response.LoginResponse;
import com.spring_commerce.security.response.MessageResponse;
import com.spring_commerce.security.services.UserDetailsImplementation;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManger;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    // Handles user authentication and returns a JWT cookie upon success
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(
            @RequestBody final LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = this.authenticationManger
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

        } catch (final AuthenticationException e) {
            // Returns error response for failed authentication
            final Map<String, Object> error = new HashMap<>();
            error.put("message", "Bad Credentials");
            error.put("status", false);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final UserDetailsImplementation userDetails =
                (UserDetailsImplementation) authentication.getPrincipal();
        final ResponseCookie jwtCookie =
                this.jwtUtils.generateJwtCookie(userDetails);

        final List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        final LoginResponse response = new LoginResponse(userDetails.getId(),
                userDetails.getUsername(), roles);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(response);

    }

    // Handles user registration
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody final SignupRequest signupRequest) {
        if (this.userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(
                    new MessageResponse("Error: Username is already taken."));
        }

        if (this.userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(
                    new MessageResponse("Error: Email is already in use."));
        }

        // Create new user account
        final User user =
                new User(signupRequest.getUsername(), signupRequest.getEmail(),
                        this.encoder.encode(signupRequest.getPassword()));

        final Set<String> strRoles = signupRequest.getRoles();
        final Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            // Assign default role
            final Role userRole =
                    this.roleRepository.findByAppRole(AppRole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException(
                                    "Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        roles.add(this.roleRepository
                                .findByAppRole(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException(
                                        "Error: Role is not found.")));
                        break;
                    case "seller":
                        roles.add(this.roleRepository
                                .findByAppRole(AppRole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException(
                                        "Error: Role is not found.")));
                        break;
                    default:
                        roles.add(this.roleRepository
                                .findByAppRole(AppRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException(
                                        "Error: Role is not found.")));
                }
            });
        }

        user.setRoles(roles);
        this.userRepository.save(user);

        return ResponseEntity
                .ok(new MessageResponse("User registered successfully!"));
    }

    // Returns the current username if authenticated; otherwise, returns an empty
    // string
    @GetMapping("/username")
    public String getUsername(final Authentication authentication) {
        return (authentication != null) ? authentication.getName() : "";
    }

    // Returns authenticated user details
    @GetMapping("/user")
    public ResponseEntity<?> getUser(final Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("You must be logged in."));
        }

        // Extract user details from authentication principal
        final UserDetailsImplementation userDetails =
                (UserDetailsImplementation) authentication.getPrincipal();

        // Convert authorities to a list of role names
        final List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Create response with user ID, username, and roles
        final LoginResponse response = new LoginResponse(userDetails.getId(),
                userDetails.getUsername(), roles);

        return ResponseEntity.ok().body(response);
    }

    // Handles user sign-out by clearing the JWT cookie
    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser() {
        final ResponseCookie cookie = this.jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()) // Clear the
                                                                   // JWT cookie
                .body(new MessageResponse("You have been signed out."));
    }
}
