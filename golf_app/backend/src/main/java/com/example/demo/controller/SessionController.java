package com.example.demo.controller;

import com.example.demo.model.Session;
import com.example.demo.model.Shot;
import com.example.demo.service.CsvService;
import com.example.demo.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "*") // Enable CORS - adjust this in production
public class SessionController {
    
    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private CsvService csvService;
    
    /**
     * Get all sessions
     */
    @GetMapping
    public ResponseEntity<List<Session>> getAllSessions() {
        List<Session> sessions = sessionService.getAllSessions();
        return new ResponseEntity<>(sessions, HttpStatus.OK);
    }
    
    /**
     * Get a specific session by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Session> getSessionById(@PathVariable Long id) {
        Optional<Session> session = sessionService.getSessionById(id);
        return session.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Upload a CSV file and create a new session
     */
    @PostMapping("/upload")
    public ResponseEntity<Object> uploadCsvFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "source", required = false, defaultValue = "GARMIN_R10") String source) {
        
        try {
            Session session;
            String locationString = location != null ? location : "";
            
            if ("AWESOME_GOLF".equalsIgnoreCase(source)) {
                session = csvService.processAwesomeGolfCsv(file, title, locationString);
            } else {
                // Default to Garmin R10 processing
                session = csvService.processGarminR10Csv(file, title, locationString);
                session.setSourceType("GARMIN_R10");
            }
            
            return new ResponseEntity<>(session, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error processing CSV file: " + e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Update a session
     */
    @PutMapping("/{id}")
    public ResponseEntity<Session> updateSession(@PathVariable Long id, @RequestBody Session sessionDetails) {
        Session updatedSession = sessionService.updateSession(id, sessionDetails);
        if (updatedSession != null) {
            return new ResponseEntity<>(updatedSession, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    /**
     * Delete a session
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        boolean deleted = sessionService.deleteSession(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    /**
     * Get all shots for a session
     */
    @GetMapping("/{id}/shots")
    public ResponseEntity<List<Shot>> getSessionShots(@PathVariable Long id) {
        // Verify session exists
        if (!sessionService.getSessionById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        List<Shot> shots = sessionService.getShotsBySessionId(id);
        return new ResponseEntity<>(shots, HttpStatus.OK);
    }
    
    /**
     * Get statistics for a session
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getSessionStats(@PathVariable Long id) {
        // Verify session exists
        if (!sessionService.getSessionById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Map<String, Object> stats = sessionService.getSessionStats(id);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }
    
    /**
     * Search for sessions by title
     */
    @GetMapping("/search")
    public ResponseEntity<List<Session>> searchSessions(@RequestParam String title) {
        List<Session> sessions = sessionService.searchSessionsByTitle(title);
        return new ResponseEntity<>(sessions, HttpStatus.OK);
    }
}