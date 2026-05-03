package com.example.demo.repository;

import com.example.demo.model.BookRequest;
import com.example.demo.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRequestRepository extends JpaRepository<BookRequest, Long> {
    List<BookRequest> findByUserId(Long userId);
    List<BookRequest> findByStatus(RequestStatus status);
}
