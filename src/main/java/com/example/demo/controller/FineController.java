package com.example.demo.controller;

import com.example.demo.model.FineLog;
import com.example.demo.model.FineStatus;
import com.example.demo.model.User;
import com.example.demo.repository.FineLogRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fines")
public class FineController {

    @Autowired
    private FineLogRepository fineLogRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all fines (Admin/Librarian)
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public List<FineLog> getAllFines() {
        return fineLogRepository.findAll();
    }

    // Get my fines (User)
    @GetMapping("/my")
    public List<FineLog> getMyFines(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return fineLogRepository.findByUserId(userDetails.getUser().getId());
    }

    // Pay fine
    @PostMapping("/pay/{fineId}")
    public ResponseEntity<?> payFine(@PathVariable Long fineId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        FineLog fineLog = fineLogRepository.findById(fineId).orElse(null);
        if (fineLog == null) return ResponseEntity.notFound().build();

        // Security check: Only the user who owns the fine or an admin can pay it
        if (!fineLog.getUser().getId().equals(userDetails.getUser().getId()) && 
            !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_LIBRARIAN"))) {
            return ResponseEntity.status(403).body("Unauthorized");
        }

        if (fineLog.getStatus() == FineStatus.CLEARED) {
            return ResponseEntity.badRequest().body("Fine already cleared");
        }

        fineLog.setStatus(FineStatus.CLEARED);
        fineLogRepository.save(fineLog);

        // Update user fine balance
        User user = fineLog.getUser();
        user.setFineBalance(user.getFineBalance().subtract(fineLog.getAmount()));
        if (user.getFineBalance().compareTo(BigDecimal.ZERO) < 0) {
            user.setFineBalance(BigDecimal.ZERO);
        }
        userRepository.save(user);

        return ResponseEntity.ok("Fine paid successfully");
    }

    // Admin manually adds a fine
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<?> addFine(@RequestBody Map<String, String> payload) {
        Long userId = Long.parseLong(payload.get("userId"));
        BigDecimal amount = new BigDecimal(payload.get("amount"));
        String reason = payload.get("reason");

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.badRequest().body("User not found");

        FineLog fineLog = new FineLog();
        fineLog.setUser(user);
        fineLog.setAmount(amount);
        fineLog.setReason(reason);
        fineLog.setStatus(FineStatus.PENDING);
        fineLogRepository.save(fineLog);

        user.setFineBalance(user.getFineBalance().add(amount));
        userRepository.save(user);

        return ResponseEntity.ok(fineLog);
    }
}
