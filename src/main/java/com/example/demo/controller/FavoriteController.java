package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Book;
import com.example.demo.model.Favorite;
import com.example.demo.model.User;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.security.CustomUserDetails;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private BookRepository bookRepository;

    // Get all favorites for user
    @GetMapping("/my")
    public List<Favorite> getMyFavorites(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return favoriteRepository.findByUserId(userDetails.getUser().getId());
    }

    // Add to favorites
    @PostMapping("/add")
    public ResponseEntity<?> addFavorite(@RequestBody Map<String, Long> payload, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long bookId = payload.get("bookId");
        User user = userDetails.getUser();

        if (favoriteRepository.existsByUserIdAndBookId(user.getId(), bookId)) {
            return ResponseEntity.badRequest().body("Already in favorites");
        }

        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            return ResponseEntity.badRequest().body("Book not found");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setBook(book);
        return ResponseEntity.ok(favoriteRepository.save(favorite));
    }

    // Remove from favorites
    @DeleteMapping("/remove/{bookId}")
    public ResponseEntity<?> removeFavorite(@PathVariable Long bookId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Favorite favorite = favoriteRepository.findByUserIdAndBookId(userDetails.getUser().getId(), bookId).orElse(null);
        if (favorite == null) {
            return ResponseEntity.notFound().build();
        }

        favoriteRepository.delete(favorite);
        return ResponseEntity.ok().build();
    }
}
