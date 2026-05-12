package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Book;
import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionStatus;
import com.example.demo.model.User;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetails;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    // Issue Book
    @PostMapping("/issue")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> issueBook(@RequestBody Map<String, Long> payload) {
        Long bookId = payload.get("bookId");
        Long userId = payload.get("userId");

        Book book = bookRepository.findById(bookId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (book == null || user == null) {
            return ResponseEntity.badRequest().body("Invalid book or user ID");
        }
        if (book.getAvailableCopies() <= 0) {
            return ResponseEntity.badRequest().body("Book not available");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        Transaction tx = new Transaction();
        tx.setBook(book);
        tx.setUser(user);
        tx.setIssueDate(LocalDate.now());
        tx.setDueDate(LocalDate.now().plusDays(14)); // 14 days borrowing period
        tx.setStatus(TransactionStatus.BORROWED);

        return ResponseEntity.ok(transactionRepository.save(tx));
    }

    // Return Book
    @PostMapping("/return/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> returnBook(@PathVariable Long id) {
        Transaction tx = transactionRepository.findById(id).orElse(null);
        if (tx == null) {
            return ResponseEntity.notFound().build();
        }
        if (tx.getStatus() == TransactionStatus.RETURNED) {
            return ResponseEntity.badRequest().body("Already returned");
        }

        tx.setStatus(TransactionStatus.RETURNED);
        tx.setReturnDate(LocalDate.now());

        Book book = tx.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return ResponseEntity.ok(transactionRepository.save(tx));
    }

    // Get all transactions (Admin/Librarian)
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Get active transactions (Admin/Librarian)
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public List<Transaction> getActiveTransactions() {
        return transactionRepository.findByStatus(TransactionStatus.BORROWED);
    }

    // Get my transactions (User)
    @GetMapping("/my")
    public List<Transaction> getMyTransactions(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return transactionRepository.findByUserId(userDetails.getUser().getId());
    }

    // User Borrow Book directly
    @PostMapping("/user-borrow/{bookId}")
    public ResponseEntity<?> userBorrowBook(@PathVariable Long bookId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            return ResponseEntity.badRequest().body("Invalid book");
        }
        if (book.getAvailableCopies() <= 0) {
            return ResponseEntity.badRequest().body("Book out of stock");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        Transaction tx = new Transaction();
        tx.setBook(book);
        tx.setUser(userDetails.getUser());
        tx.setIssueDate(LocalDate.now());
        tx.setDueDate(LocalDate.now().plusDays(14));
        tx.setStatus(TransactionStatus.BORROWED);

        return ResponseEntity.ok(transactionRepository.save(tx));
    }

    // User Return Book directly
    @PostMapping("/user-return/{txId}")
    public ResponseEntity<?> userReturnBook(@PathVariable Long txId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Transaction tx = transactionRepository.findById(txId).orElse(null);
        if (tx == null) {
            return ResponseEntity.notFound().build();
        }
        if (!tx.getUser().getId().equals(userDetails.getUser().getId())) {
            return ResponseEntity.status(403).body("Not your book");
        }
        if (tx.getStatus() == TransactionStatus.RETURNED) {
            return ResponseEntity.badRequest().body("Already returned");
        }

        tx.setStatus(TransactionStatus.RETURNED);
        tx.setReturnDate(LocalDate.now());

        Book book = tx.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return ResponseEntity.ok(transactionRepository.save(tx));
    }
}
