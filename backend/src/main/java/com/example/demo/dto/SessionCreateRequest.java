package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Request DTO for creating a new session
 */
@Schema(description = "Request for creating a new golf session")
public class SessionCreateRequest {
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    @Schema(description = "Title of the golf session", example = "Practice Session - Driver Work", required = true)
    private String title;
    
    @Size(max = 255, message = "Location must be less than 255 characters")
    @Schema(description = "Location where the session was recorded", example = "Driving Range")
    private String location;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Date and time when the session was recorded", example = "2024-01-15T09:00:00")
    private LocalDateTime sessionDate;
    
    @Pattern(regexp = "^(GARMIN_R10|AWESOME_GOLF)$", message = "Source type must be either GARMIN_R10 or AWESOME_GOLF")
    @Schema(description = "Source type of the session data", example = "GARMIN_R10", allowableValues = {"GARMIN_R10", "AWESOME_GOLF"})
    private String sourceType;
    
    // Constructors
    public SessionCreateRequest() {}
    
    public SessionCreateRequest(String title, String location, LocalDateTime sessionDate, String sourceType) {
        this.title = title;
        this.location = location;
        this.sessionDate = sessionDate;
        this.sourceType = sourceType;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public LocalDateTime getSessionDate() {
        return sessionDate;
    }
    
    public void setSessionDate(LocalDateTime sessionDate) {
        this.sessionDate = sessionDate;
    }
    
    public String getSourceType() {
        return sourceType;
    }
    
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
}