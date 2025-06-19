package com.example.demo.repository;

import com.example.demo.model.Shot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShotRepository extends JpaRepository<Shot, Long> {
    
    // Find all shots for a specific session
    List<Shot> findBySessionIdOrderByShotNumber(Long sessionId);
    
    // Find shots by club type for a specific session
    List<Shot> findBySessionIdAndClubOrderByShotNumber(Long sessionId, String club);
    
    // Find shots with carry distance greater than the specified value
    List<Shot> findBySessionIdAndCarryDistanceGreaterThanEqualOrderByShotNumber(Long sessionId, Double minDistance);
    
    // Find shots with total distance greater than the specified value
    List<Shot> findBySessionIdAndTotalDistanceGreaterThanEqualOrderByShotNumber(Long sessionId, Double minDistance);
}