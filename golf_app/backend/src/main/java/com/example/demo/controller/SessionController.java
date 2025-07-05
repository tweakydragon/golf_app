package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.SessionCreateRequest;
import com.example.demo.dto.SessionSummaryResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.exception.InvalidFileFormatException;
import com.example.demo.exception.FileProcessingException;
import com.example.demo.model.Session;
import com.example.demo.model.Shot;
import com.example.demo.service.CsvService;
import com.example.demo.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "*") // Enable CORS - adjust this in production
@Validated
@Tag(name = "Session Management", description = "API endpoints for managing golf sessions and shots")
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
    @Operation(
        summary = "Get all golf sessions",
        description = "Retrieve a list of all golf sessions ordered by upload date (newest first)",
        tags = {"Session Management"}
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved sessions",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<Session>>> getAllSessions() {
        try {
            List<Session> sessions = sessionService.getAllSessions();
            return ResponseEntity.ok(ApiResponse.success(sessions, "Sessions retrieved successfully"));
        } catch (Exception e) {
            logger.error("Error retrieving sessions", e);
            throw new RuntimeException("Failed to retrieve sessions");
        }
    }
    
    /**
     * Get a specific session by ID
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get session by ID",
        description = "Retrieve detailed information about a specific golf session",
        tags = {"Session Management"}
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved session",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Session.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Session not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<Session>> getSessionById(
            @Parameter(description = "ID of the session to retrieve", required = true)
            @PathVariable @Min(value = 1, message = "Session ID must be positive") Long id) {
        
        Optional<Session> session = sessionService.getSessionById(id);
        if (session.isEmpty()) {
            throw new ResourceNotFoundException("Session", id);
        }
        
        return ResponseEntity.ok(ApiResponse.success(session.get(), "Session retrieved successfully"));
    }
    
    /**
     * Upload a CSV file and create a new session
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Upload CSV file and create session",
        description = "Upload a CSV file containing golf shot data and create a new session. " +
                     "Supports both Garmin R10 and Awesome Golf formats.",
        tags = {"Session Management", "File Upload"}
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Session created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Session.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid file format or validation error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "413",
            description = "File size too large",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<Session>> uploadCsvFile(
            @Parameter(description = "CSV file containing golf shot data", required = true)
            @RequestParam("file") MultipartFile file,
            
            @Parameter(description = "Title for the new session", required = true)
            @RequestParam("title") String title,
            
            @Parameter(description = "Location where the session was recorded")
            @RequestParam(value = "location", required = false) String location,
            
            @Parameter(description = "Source type of the CSV data", schema = @Schema(allowableValues = {"GARMIN_R10", "AWESOME_GOLF"}))
            @RequestParam(value = "source", required = false, defaultValue = "GARMIN_R10") String source) {
        
        // Validate file
        if (file.isEmpty()) {
            throw new InvalidFileFormatException("File is empty");
        }
        
        // Validate title
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Title is required");
        }
        
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
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(session, "Session created successfully"));
            
        } catch (Exception e) {
            logger.error("Error processing CSV file: " + e.getMessage(), e);
            throw new FileProcessingException("Failed to process CSV file", e);
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