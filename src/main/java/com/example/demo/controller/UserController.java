package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.example.demo.repository.TransactionRepository transactionRepository;

    @Autowired
    private com.example.demo.repository.FavoriteRepository favoriteRepository;

    @Autowired
    private com.example.demo.repository.FineLogRepository fineLogRepository;

    @Autowired
    private com.example.demo.repository.BookRequestRepository bookRequestRepository;

    @Autowired
    private com.example.demo.repository.BookRepository bookRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/profile")
    public org.springframework.http.ResponseEntity<?> getProfile(@org.springframework.security.core.annotation.AuthenticationPrincipal com.example.demo.security.CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getUser().getId()).orElse(null);
        if (user == null) {
            return org.springframework.http.ResponseEntity.notFound().build();
        }
        return org.springframework.http.ResponseEntity.ok(user);
    }

    @org.springframework.web.bind.annotation.PutMapping("/profile")
    public org.springframework.http.ResponseEntity<?> updateProfile(
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.example.demo.security.CustomUserDetails userDetails,
            @org.springframework.web.bind.annotation.RequestBody java.util.Map<String, String> payload) {

        User user = userRepository.findById(userDetails.getUser().getId()).orElse(null);
        if (user == null) {
            return org.springframework.http.ResponseEntity.notFound().build();
        }

        if (payload.containsKey("username")) {
            String newUsername = payload.get("username");
            if (!newUsername.equals(user.getUsername()) && userRepository.existsByUsername(newUsername)) {
                return org.springframework.http.ResponseEntity.badRequest().body("Username already exists. Please try a different name.");
            }
            user.setUsername(newUsername);
        }
        if (payload.containsKey("firstName")) {
            user.setFirstName(payload.get("firstName"));
        }
        if (payload.containsKey("lastName")) {
            user.setLastName(payload.get("lastName"));
        }
        if (payload.containsKey("email")) {
            user.setEmail(payload.get("email"));
        }
        if (payload.containsKey("bio")) {
            user.setBio(payload.get("bio"));
        }
        if (payload.containsKey("photoUrl")) {
            user.setPhotoUrl(payload.get("photoUrl"));
        }

        return org.springframework.http.ResponseEntity.ok(userRepository.save(user));
    }

    @org.springframework.web.bind.annotation.PostMapping("/change-password")
    public org.springframework.http.ResponseEntity<?> changePassword(
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.example.demo.security.CustomUserDetails userDetails,
            @org.springframework.web.bind.annotation.RequestBody java.util.Map<String, String> payload,
            @Autowired org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {

        User user = userRepository.findById(userDetails.getUser().getId()).orElse(null);
        if (user == null) {
            return org.springframework.http.ResponseEntity.notFound().build();
        }

        String currentPassword = payload.get("currentPassword");
        String newPassword = payload.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return org.springframework.http.ResponseEntity.badRequest().body("Missing passwords");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            return org.springframework.http.ResponseEntity.badRequest().body("Incorrect current password");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return org.springframework.http.ResponseEntity.ok("Password changed successfully");
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/account")
    @org.springframework.transaction.annotation.Transactional
    public org.springframework.http.ResponseEntity<?> deleteAccount(
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.example.demo.security.CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();

        // Release borrowed books
        List<com.example.demo.model.Transaction> activeTxs = transactionRepository.findByUserIdAndStatus(userId, com.example.demo.model.TransactionStatus.BORROWED);
        for (com.example.demo.model.Transaction tx : activeTxs) {
            com.example.demo.model.Book book = tx.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
        }

        // Delete dependencies
        transactionRepository.deleteAll(transactionRepository.findByUserId(userId));
        favoriteRepository.deleteAll(favoriteRepository.findByUserId(userId));
        fineLogRepository.deleteAll(fineLogRepository.findByUserId(userId));
        bookRequestRepository.deleteAll(bookRequestRepository.findByUserId(userId));

        userRepository.deleteById(userId);
        return org.springframework.http.ResponseEntity.ok("Account deleted successfully");
    }
}
