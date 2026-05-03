package com.example.demo.repository;

import com.example.demo.model.FineLog;
import com.example.demo.model.FineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FineLogRepository extends JpaRepository<FineLog, Long> {
    List<FineLog> findByUserId(Long userId);
    List<FineLog> findByUserIdAndStatus(Long userId, FineStatus status);
    List<FineLog> findByStatus(FineStatus status);
}
