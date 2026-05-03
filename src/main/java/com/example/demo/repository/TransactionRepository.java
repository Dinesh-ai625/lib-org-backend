package com.example.demo.repository;

import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);
    List<Transaction> findByUserIdAndStatus(Long userId, TransactionStatus status);
    List<Transaction> findByStatus(TransactionStatus status);
}
