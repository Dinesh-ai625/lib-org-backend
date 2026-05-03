package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.SignupRequest;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            final String jwt = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(jwt, userDetails.getUser().getRole().name(), userDetails.getUsername()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists. Please try a different name.");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(signupRequest.getPassword()));

        try {
            if (signupRequest.getRole() != null && !signupRequest.getRole().isEmpty()) {
                user.setRole(Role.valueOf(signupRequest.getRole().toUpperCase()));
            } else {
                user.setRole(Role.USER);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: Invalid role specified");
        }

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }
}
