package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.FineLog;
import com.example.demo.model.FineStatus;

@Repository
public interface FineLogRepository extends JpaRepository<FineLog, Long> {

    List<FineLog> findByUserId(Long userId);

    List<FineLog> findByUserIdAndStatus(Long userId, FineStatus status);

    List<FineLog> findByStatus(FineStatus status);
}
