package com.example.demo.service;

import com.example.demo.model.FineLog;
import com.example.demo.model.FineStatus;
import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionStatus;
import com.example.demo.model.User;
import com.example.demo.repository.FineLogRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CronJobService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private FineLogRepository fineLogRepository;

    @Autowired
    private UserRepository userRepository;

    // Runs every day at midnight (00:00:00)
    @Scheduled(cron = "0 0 0 * * ?")
    public void calculateOverdueFines() {
        System.out.println("Running midnight cron job for overdue fines...");

        List<Transaction> activeTransactions = transactionRepository.findByStatus(TransactionStatus.BORROWED);
        LocalDate today = LocalDate.now();

        for (Transaction tx : activeTransactions) {
            if (tx.getDueDate().isBefore(today)) {
                // Change status to overdue
                tx.setStatus(TransactionStatus.OVERDUE);
                transactionRepository.save(tx);

                // Calculate fine: e.g., $1.00 per day overdue
                long daysOverdue = ChronoUnit.DAYS.between(tx.getDueDate(), today);
                BigDecimal fineAmount = new BigDecimal(daysOverdue * 1.00); // Base fine on first day overdue

                User user = tx.getUser();

                FineLog fineLog = new FineLog();
                fineLog.setUser(user);
                fineLog.setAmount(fineAmount);
                fineLog.setReason("Overdue book: " + tx.getBook().getTitle());
                fineLog.setStatus(FineStatus.PENDING);
                fineLogRepository.save(fineLog);

                user.setFineBalance(user.getFineBalance().add(fineAmount));
                userRepository.save(user);

                System.out.println("Applied fine of $" + fineAmount + " to user " + user.getUsername() + " for overdue book " + tx.getBook().getTitle());
            }
        }
    }
}
