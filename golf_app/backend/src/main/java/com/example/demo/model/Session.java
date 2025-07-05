package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Schema(description = "Golf session containing multiple shots")
public class Session {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the session", example = "1")
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    @Schema(description = "Title of the golf session", example = "Practice Session - Driver Work", required = true)
    private String title;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Date and time when the session was uploaded", example = "2024-01-15T10:30:00")
    private LocalDateTime uploadDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Date and time when the session was recorded", example = "2024-01-15T09:00:00")
    private LocalDateTime sessionDate;
    
    @Size(max = 255, message = "Location must be less than 255 characters")
    @Schema(description = "Location where the session was recorded", example = "Driving Range")
    private String location;
    
    @Pattern(regexp = "^(GARMIN_R10|AWESOME_GOLF)$", message = "Source type must be either GARMIN_R10 or AWESOME_GOLF")
    @Schema(description = "Source type of the session data", example = "GARMIN_R10", allowableValues = {"GARMIN_R10", "AWESOME_GOLF"})
    private String sourceType;  // "GARMIN_R10" or "AWESOME_GOLF"
    
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "List of shots in this session")
    private List<Shot> shots = new ArrayList<>();
    
    // Default constructor required by JPA
    public Session() {
    }
    
    public Session(String title) {
        this.title = title;
        this.uploadDate = LocalDateTime.now();
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public List<Shot> getShots() {
        return shots;
    }

    public void setShots(List<Shot> shots) {
        this.shots = shots;
    }
    
    // Helper method to add a shot to this session
    public void addShot(Shot shot) {
        shots.add(shot);
        shot.setSession(this);
    }
    
    // Helper method to remove a shot from this session
    public void removeShot(Shot shot) {
        shots.remove(shot);
        shot.setSession(null);
    }
}