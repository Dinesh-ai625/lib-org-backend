package com.example.demo.controller;

import com.example.demo.dto.BookDto;
import com.example.demo.model.Book;
import com.example.demo.model.Rack;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.RackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RackRepository rackRepository;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/search")
    public List<Book> searchBooks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String filterBy
    ) {
        if (query == null || query.trim().isEmpty()) {
            return bookRepository.findAll();
        }
        
        if (filterBy != null) {
            switch (filterBy.toLowerCase()) {
                case "author": return bookRepository.findByAuthorContainingIgnoreCase(query);
                case "genre": return bookRepository.findByGenreIgnoreCase(query);
                case "category": return bookRepository.findByCategoryIgnoreCase(query);
            }
        }
        
        // Default to title search
        return bookRepository.findByTitleContainingIgnoreCase(query);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> createBook(@RequestBody BookDto bookDto) {
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setGenre(bookDto.getGenre());
        book.setCategory(bookDto.getCategory());
        book.setIsbn(bookDto.getIsbn());
        book.setTotalCopies(bookDto.getTotalCopies());
        book.setAvailableCopies(bookDto.getTotalCopies());
        if (bookDto.getCoverUrl() != null) book.setCoverUrl(bookDto.getCoverUrl());
        if (bookDto.getDescription() != null) book.setDescription(bookDto.getDescription());

        if (bookDto.getRackId() != null) {
            Rack rack = rackRepository.findById(bookDto.getRackId()).orElse(null);
            if (rack == null) return ResponseEntity.badRequest().body("Rack not found");
            book.setRack(rack);
        }

        return ResponseEntity.ok(bookRepository.save(book));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) return ResponseEntity.notFound().build();
        bookRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Move book to a different rack (or null to unassign)
    @PatchMapping("/{id}/rack")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> updateBookRack(@PathVariable Long id, @RequestBody java.util.Map<String, Object> payload) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) return ResponseEntity.notFound().build();

        Object rackIdObj = payload.get("rackId");
        if (rackIdObj == null && payload.containsKey("rackId")) {
            book.setRack(null);
        } else if (rackIdObj != null) {
            Long rackId = Long.valueOf(rackIdObj.toString());
            Rack rack = rackRepository.findById(rackId).orElse(null);
            if (rack == null) return ResponseEntity.badRequest().body("Rack not found");
            book.setRack(rack);
        }

        if (payload.containsKey("position")) {
            Object posObj = payload.get("position");
            book.setPosition(posObj != null ? Integer.valueOf(posObj.toString()) : null);
        }

        return ResponseEntity.ok(bookRepository.save(book));
    }

    // Bulk update books (for swapping/reordering)
    @PatchMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> updateBooksBulk(@RequestBody List<java.util.Map<String, Object>> updates) {
        for (java.util.Map<String, Object> update : updates) {
            Long id = Long.valueOf(update.get("id").toString());
            Book book = bookRepository.findById(id).orElse(null);
            if (book != null) {
                if (update.containsKey("rackId")) {
                    Object rId = update.get("rackId");
                    if (rId == null) book.setRack(null);
                    else {
                        Rack rack = rackRepository.findById(Long.valueOf(rId.toString())).orElse(null);
                        if (rack != null) book.setRack(rack);
                    }
                }
                if (update.containsKey("position")) {
                    Object pos = update.get("position");
                    book.setPosition(pos != null ? Integer.valueOf(pos.toString()) : null);
                }
                bookRepository.save(book);
            }
        }
        return ResponseEntity.ok().build();
    }
}
