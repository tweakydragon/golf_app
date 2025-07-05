package com.example.demo.repository;

import com.example.demo.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    
    // Find sessions ordered by upload date (newest first)
    List<Session> findAllByOrderByUploadDateDesc();
    
    // Find sessions by title containing the search term (case insensitive)
    List<Session> findByTitleContainingIgnoreCaseOrderByUploadDateDesc(String titleSearch);
    
    // Find sessions by location containing the search term (case insensitive)
    List<Session> findByLocationContainingIgnoreCaseOrderByUploadDateDesc(String locationSearch);
}