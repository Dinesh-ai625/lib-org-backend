package com.example.demo.controller;

import com.example.demo.model.BookRequest;
import com.example.demo.model.RequestStatus;
import com.example.demo.model.User;
import com.example.demo.repository.BookRequestRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
public class BookRequestController {

    @Autowired
    private BookRequestRepository bookRequestRepository;

    @Autowired
    private UserRepository userRepository;

    // User submits a request
    @PostMapping("/submit")
    public ResponseEntity<?> submitRequest(@RequestBody Map<String, String> payload, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String title = payload.get("bookTitle");
        String author = payload.get("author");

        if (title == null || title.isEmpty() || author == null || author.isEmpty()) {
            return ResponseEntity.badRequest().body("Title and Author are required");
        }

        BookRequest request = new BookRequest();
        request.setUser(userDetails.getUser());
        request.setBookTitle(title);
        request.setAuthor(author);
        request.setStatus(RequestStatus.PENDING);
        
        return ResponseEntity.ok(bookRequestRepository.save(request));
    }

    // User gets their own requests
    @GetMapping("/my")
    public List<BookRequest> getMyRequests(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return bookRequestRepository.findByUserId(userDetails.getUser().getId());
    }

    // Admin/Librarian gets all requests
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public List<BookRequest> getAllRequests() {
        return bookRequestRepository.findAll();
    }

    // Admin updates status
    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> updateRequestStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        BookRequest request = bookRequestRepository.findById(id).orElse(null);
        if (request == null) return ResponseEntity.notFound().build();

        try {
            RequestStatus status = RequestStatus.valueOf(payload.get("status").toUpperCase());
            request.setStatus(status);
            return ResponseEntity.ok(bookRequestRepository.save(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status");
        }
    }

    // User cancels their own request
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<?> cancelRequest(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        BookRequest request = bookRequestRepository.findById(id).orElse(null);
        if (request == null) return ResponseEntity.notFound().build();
        if (!request.getUser().getId().equals(userDetails.getUser().getId())) return ResponseEntity.status(403).body("Not your request");

        bookRequestRepository.delete(request);
        return ResponseEntity.ok("Request cancelled");
    }
}
