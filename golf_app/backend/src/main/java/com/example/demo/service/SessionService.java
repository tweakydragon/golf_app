package com.example.demo.service;

import com.example.demo.model.Session;
import com.example.demo.model.Shot;
import com.example.demo.repository.SessionRepository;
import com.example.demo.repository.ShotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SessionService {
    
    @Autowired
    private SessionRepository sessionRepository;
    
    @Autowired
    private ShotRepository shotRepository;
    
    /**
     * Get all sessions ordered by upload date (newest first)
     */
    public List<Session> getAllSessions() {
        return sessionRepository.findAllByOrderByUploadDateDesc();
    }
    
    /**
     * Get a specific session by ID
     */
    public Optional<Session> getSessionById(Long id) {
        return sessionRepository.findById(id);
    }
    
    /**
     * Create a new session
     */
    public Session createSession(Session session) {
        return sessionRepository.save(session);
    }
    
    /**
     * Update an existing session
     */
    public Session updateSession(Long id, Session sessionDetails) {
        Optional<Session> optionalSession = sessionRepository.findById(id);
        
        if (optionalSession.isPresent()) {
            Session existingSession = optionalSession.get();
            existingSession.setTitle(sessionDetails.getTitle());
            existingSession.setLocation(sessionDetails.getLocation());
            existingSession.setSessionDate(sessionDetails.getSessionDate());
            return sessionRepository.save(existingSession);
        }
        
        return null;
    }
    
    /**
     * Delete a session
     */
    public boolean deleteSession(Long id) {
        Optional<Session> optionalSession = sessionRepository.findById(id);
        if (optionalSession.isPresent()) {
            sessionRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * Get all shots for a specific session
     */
    public List<Shot> getShotsBySessionId(Long sessionId) {
        return shotRepository.findBySessionIdOrderByShotNumber(sessionId);
    }
    
    /**
     * Search for sessions by title
     */
    public List<Session> searchSessionsByTitle(String searchTerm) {
        return sessionRepository.findByTitleContainingIgnoreCaseOrderByUploadDateDesc(searchTerm);
    }
    
    /**
     * Calculate statistics for a session
     */
    public Map<String, Object> getSessionStats(Long sessionId) {
        List<Shot> shots = shotRepository.findBySessionIdOrderByShotNumber(sessionId);
        Map<String, Object> stats = new HashMap<>();
        
        if (shots.isEmpty()) {
            return stats;
        }
        
        // Overall stats
        stats.put("totalShots", shots.size());
        
        // Average distances
        double avgCarryDistance = shots.stream()
            .filter(s -> s.getCarryDistance() != null)
            .mapToDouble(Shot::getCarryDistance)
            .average()
            .orElse(0.0);
        
        double avgTotalDistance = shots.stream()
            .filter(s -> s.getTotalDistance() != null)
            .mapToDouble(Shot::getTotalDistance)
            .average()
            .orElse(0.0);
        
        stats.put("avgCarryDistance", Math.round(avgCarryDistance * 10.0) / 10.0);
        stats.put("avgTotalDistance", Math.round(avgTotalDistance * 10.0) / 10.0);
        
        // Average ball speed
        double avgBallSpeed = shots.stream()
            .filter(s -> s.getBallSpeed() != null)
            .mapToDouble(Shot::getBallSpeed)
            .average()
            .orElse(0.0);
        stats.put("avgBallSpeed", Math.round(avgBallSpeed * 10.0) / 10.0);
        
        // Club breakdown
        Map<String, Long> clubCounts = shots.stream()
            .filter(s -> s.getClub() != null && !s.getClub().isEmpty())
            .collect(Collectors.groupingBy(Shot::getClub, Collectors.counting()));
        stats.put("clubCounts", clubCounts);
        
        // Club statistics - average distance per club
        Map<String, Map<String, Double>> clubStats = new HashMap<>();
        shots.stream()
            .filter(s -> s.getClub() != null && !s.getClub().isEmpty())
            .collect(Collectors.groupingBy(Shot::getClub))
            .forEach((club, clubShots) -> {
                Map<String, Double> clubStat = new HashMap<>();
                
                // Average carry distance for this club
                double clubAvgCarry = clubShots.stream()
                    .filter(s -> s.getCarryDistance() != null)
                    .mapToDouble(Shot::getCarryDistance)
                    .average()
                    .orElse(0.0);
                clubStat.put("avgCarry", Math.round(clubAvgCarry * 10.0) / 10.0);
                
                // Average total distance for this club
                double clubAvgTotal = clubShots.stream()
                    .filter(s -> s.getTotalDistance() != null)
                    .mapToDouble(Shot::getTotalDistance)
                    .average()
                    .orElse(0.0);
                clubStat.put("avgTotal", Math.round(clubAvgTotal * 10.0) / 10.0);
                
                // Average ball speed for this club
                double clubAvgBallSpeed = clubShots.stream()
                    .filter(s -> s.getBallSpeed() != null)
                    .mapToDouble(Shot::getBallSpeed)
                    .average()
                    .orElse(0.0);
                clubStat.put("avgBallSpeed", Math.round(clubAvgBallSpeed * 10.0) / 10.0);
                
                clubStats.put(club, clubStat);
            });
        
        stats.put("clubStats", clubStats);
        
        return stats;
    }
}