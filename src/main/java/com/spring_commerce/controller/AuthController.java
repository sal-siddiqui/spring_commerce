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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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

@Controller
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
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManger.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImplementation userDetails = (UserDetailsImplementation) authentication.getPrincipal();
            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            LoginResponse response = new LoginResponse(
                    userDetails.getId(),
                    userDetails.getUsername(),
                    roles);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(response);

        } catch (AuthenticationException e) {
            // Returns error response for failed authentication
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Bad Credentials");
            error.put("status", false);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    // Handles user registration
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken."));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use."));
        }

        // Create new user account
        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            // Assign default role
            Role userRole = roleRepository.findByAppRole(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        roles.add(roleRepository.findByAppRole(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
                        break;
                    case "seller":
                        roles.add(roleRepository.findByAppRole(AppRole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
                        break;
                    default:
                        roles.add(roleRepository.findByAppRole(AppRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

}
