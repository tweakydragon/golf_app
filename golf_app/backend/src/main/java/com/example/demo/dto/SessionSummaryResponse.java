package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Response DTO for session summary information
 */
@Schema(description = "Summary information about a golf session")
public class SessionSummaryResponse {
    
    @Schema(description = "Unique identifier for the session", example = "1")
    private Long id;
    
    @Schema(description = "Title of the golf session", example = "Practice Session - Driver Work")
    private String title;
    
    @Schema(description = "Location where the session was recorded", example = "Driving Range")
    private String location;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Date and time when the session was uploaded", example = "2024-01-15T10:30:00")
    private LocalDateTime uploadDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Date and time when the session was recorded", example = "2024-01-15T09:00:00")
    private LocalDateTime sessionDate;
    
    @Schema(description = "Source type of the session data", example = "GARMIN_R10")
    private String sourceType;
    
    @Schema(description = "Number of shots in the session", example = "25")
    private int shotCount;
    
    @Schema(description = "Average carry distance in yards", example = "245.8")
    private Double avgCarryDistance;
    
    @Schema(description = "Average total distance in yards", example = "267.2")
    private Double avgTotalDistance;
    
    @Schema(description = "Average ball speed in mph", example = "145.2")
    private Double avgBallSpeed;
    
    // Constructors
    public SessionSummaryResponse() {}
    
    public SessionSummaryResponse(Long id, String title, String location, LocalDateTime uploadDate, 
                                LocalDateTime sessionDate, String sourceType, int shotCount,
                                Double avgCarryDistance, Double avgTotalDistance, Double avgBallSpeed) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.uploadDate = uploadDate;
        this.sessionDate = sessionDate;
        this.sourceType = sourceType;
        this.shotCount = shotCount;
        this.avgCarryDistance = avgCarryDistance;
        this.avgTotalDistance = avgTotalDistance;
        this.avgBallSpeed = avgBallSpeed;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public LocalDateTime getUploadDate() {
        return uploadDate;
    }
    
    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
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
    
    public int getShotCount() {
        return shotCount;
    }
    
    public void setShotCount(int shotCount) {
        this.shotCount = shotCount;
    }
    
    public Double getAvgCarryDistance() {
        return avgCarryDistance;
    }
    
    public void setAvgCarryDistance(Double avgCarryDistance) {
        this.avgCarryDistance = avgCarryDistance;
    }
    
    public Double getAvgTotalDistance() {
        return avgTotalDistance;
    }
    
    public void setAvgTotalDistance(Double avgTotalDistance) {
        this.avgTotalDistance = avgTotalDistance;
    }
    
    public Double getAvgBallSpeed() {
        return avgBallSpeed;
    }
    
    public void setAvgBallSpeed(Double avgBallSpeed) {
        this.avgBallSpeed = avgBallSpeed;
    }
}