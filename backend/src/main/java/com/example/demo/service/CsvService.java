package com.example.demo.service;

import com.example.demo.model.Session;
import com.example.demo.model.Shot;
import com.example.demo.repository.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class CsvService {
    
    private static final Logger logger = LoggerFactory.getLogger(CsvService.class);
    
    @Autowired
    private SessionRepository sessionRepository;
    
    /**
     * Process a CSV file from Garmin R10 and save it as a Session with Shots
     * 
     * @param file The uploaded CSV file
     * @param title The title for this session
     * @param location The location where the shots were taken
     * @return The saved Session object
     * @throws IOException If there's an error reading the file
     * @throws IllegalArgumentException If the file format is invalid
     */
    public Session processGarminR10Csv(MultipartFile file, String title, String location) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        if (!isValidCsvFile(file)) {
            throw new IllegalArgumentException("Invalid file format. Please upload a CSV file");
        }
        
        // Sanitize inputs
        String sanitizedTitle = sanitizeInput(title);
        String sanitizedLocation = sanitizeInput(location);
        
        if (sanitizedTitle.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        
        Session session = new Session(sanitizedTitle);
        session.setLocation(sanitizedLocation);
        session.setUploadDate(LocalDateTime.now());
        
        // Create a buffered reader to read the CSV file
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isHeaderRead = false;
            List<String> headerColumns = new ArrayList<>();
            
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Parse header line to get column indices
                if (!isHeaderRead) {
                    String[] headers = line.split(",");
                    for (String header : headers) {
                        headerColumns.add(header.trim());
                    }
                    isHeaderRead = true;
                    continue;
                }
                
                // Process data rows
                String[] values = line.split(",");
                if (values.length != headerColumns.size()) {
                    logger.warn("Skipping malformed row: " + line);
                    continue;
                }
                
                try {
                    Shot shot = parseShot(headerColumns, values);
                    session.addShot(shot);
                } catch (Exception e) {
                    logger.warn("Error parsing shot data: " + e.getMessage());
                    // Continue processing other shots
                }
            }
        }
        
        // Save only if we have at least one valid shot
        if (session.getShots().isEmpty()) {
            throw new IllegalArgumentException("No valid shots found in the CSV file");
        }
        
        // Try to extract a date from the first shot or set to current date
        if (session.getSessionDate() == null) {
            session.setSessionDate(LocalDateTime.now());
        }
        
        return sessionRepository.save(session);
    }
    
    /**
     * Parse a single shot from CSV data
     */
    private Shot parseShot(List<String> headers, String[] values) {
        Shot shot = new Shot();
        
        // Map values to shot object based on header names
        for (int i = 0; i < headers.size(); i++) {
            if (i >= values.length) break;
            
            String header = headers.get(i).toLowerCase();
            String value = values[i].trim();
            
            // Skip empty values
            if (value.isEmpty()) continue;
            
            try {
                switch (header) {
                    case "shot":
                    case "shot number":
                        shot.setShotNumber(Integer.parseInt(value));
                        break;
                    case "club":
                        shot.setClub(sanitizeInput(value));
                        break;
                    case "ball speed":
                    case "ball speed (mph)":
                        shot.setBallSpeed(parseDouble(value));
                        break;
                    case "club head speed":
                    case "club speed":
                    case "club speed (mph)":
                        shot.setClubHeadSpeed(parseDouble(value));
                        break;
                    case "launch angle":
                    case "launch angle (deg)":
                        shot.setLaunchAngle(parseDouble(value));
                        break;
                    case "launch direction":
                    case "launch direction (deg)":
                        shot.setLaunchDirection(parseDouble(value));
                        break;
                    case "spin rate":
                    case "spin rate (rpm)":
                        shot.setSpinRate(parseDouble(value));
                        break;
                    case "spin axis":
                    case "spin axis (deg)":
                        shot.setSpinAxis(parseDouble(value));
                        break;
                    case "carry":
                    case "carry distance":
                    case "carry distance (yards)":
                        shot.setCarryDistance(parseDouble(value));
                        break;
                    case "total":
                    case "total distance":
                    case "total distance (yards)":
                        shot.setTotalDistance(parseDouble(value));
                        break;
                    case "deviation":
                    case "deviation (ft)":
                        shot.setDeviation(parseDouble(value));
                        break;
                    case "apex":
                    case "apex (ft)":
                        shot.setApex(parseDouble(value));
                        break;
                    case "attack angle":
                    case "attack angle (deg)":
                        shot.setAttackAngle(parseDouble(value));
                        break;
                    case "face angle":
                    case "face angle (deg)":
                        shot.setFaceAngle(parseDouble(value));
                        break;
                    case "face to path":
                    case "face to path (deg)":
                        shot.setFaceToPath(parseDouble(value));
                        break;
                    case "swing path":
                    case "path":
                    case "path (deg)":
                        shot.setSwingPath(parseDouble(value));
                        break;
                    case "swing plane":
                    case "plane":
                    case "plane (deg)":
                        shot.setSwingPlane(parseDouble(value));
                        break;
                    case "vertical face impact":
                    case "vertical impact (in)":
                        shot.setVerticalFaceImpact(parseDouble(value));
                        break;
                    case "horizontal face impact":
                    case "horizontal impact (in)":
                        shot.setHorizontalFaceImpact(parseDouble(value));
                        break;
                }
            } catch (NumberFormatException e) {
                logger.debug("Could not parse value for header '" + header + "': " + value);
                // Continue with other fields
            }
        }
        
        return shot;
    }
    
    /**
     * Parse a double value, handling units and comma thousands separators
     */
    private Double parseDouble(String value) {
        // Remove any non-numeric characters except decimal point and minus sign
        String cleanValue = value.replaceAll("[^\\d.-]", "");
        return Double.parseDouble(cleanValue);
    }
    
    /**
     * Check if the file is a valid CSV file
     */
    private boolean isValidCsvFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        
        // Check file extension
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            return false;
        }
        
        // Check content type (though this isn't always reliable)
        String contentType = file.getContentType();
        return contentType != null && 
               (contentType.equals("text/csv") || 
                contentType.equals("application/vnd.ms-excel") ||
                contentType.equals("application/csv") ||
                contentType.equals("text/plain"));
    }
    
    /**
     * Sanitize input to prevent XSS and injection attacks
     */
    private String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        
        // Remove any potentially harmful characters
        return input.trim()
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#x27;")
                .replaceAll("/", "&#x2F;");
    }
    
    /**
     * Process a CSV file from Awesome Golf and save it as a Session with Shots
     * 
     * @param file The uploaded CSV file
     * @param title The title for this session
     * @param location The location where the shots were taken
     * @return The saved Session object
     * @throws IOException If there's an error reading the file
     * @throws IllegalArgumentException If the file format is invalid
     */
    public Session processAwesomeGolfCsv(MultipartFile file, String title, String location) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        if (!isValidCsvFile(file)) {
            throw new IllegalArgumentException("Invalid file format. Please upload a CSV file");
        }
        
        // Sanitize inputs
        String sanitizedTitle = sanitizeInput(title);
        String sanitizedLocation = sanitizeInput(location);
        
        if (sanitizedTitle.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        
        Session session = new Session(sanitizedTitle);
        session.setLocation(sanitizedLocation);
        session.setUploadDate(LocalDateTime.now());
        session.setSourceType("AWESOME_GOLF");
        
        // Create a buffered reader to read the CSV file
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            List<String> headerColumns = new ArrayList<>();
            
            // Skip the first line (header descriptions)
            if ((line = reader.readLine()) != null) {
                // First line contains header descriptions, we'll skip it
            }
            
            // Read the actual header line with units
            if ((line = reader.readLine()) != null) {
                String[] headers = line.split(",");
                for (String header : headers) {
                    headerColumns.add(header.trim());
                }
            }
            
            // Process data rows
            int shotNumber = 1;
            LocalDateTime earliestShotTime = null;
            
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                String[] values = line.split(",");
                if (values.length < headerColumns.size()) {
                    logger.warn("Skipping malformed row: " + line);
                    continue;
                }
                
                try {
                    Shot shot = parseAwesomeGolfShot(headerColumns, values);
                    shot.setShotNumber(shotNumber++);
                    session.addShot(shot);
                    
                    // Track the earliest shot time to set as session date
                    if (shot.getShotTime() != null) {
                        if (earliestShotTime == null || shot.getShotTime().isBefore(earliestShotTime)) {
                            earliestShotTime = shot.getShotTime();
                        }
                    }
                } catch (Exception e) {
                    logger.warn("Error parsing shot data: " + e.getMessage());
                    // Continue processing other shots
                }
            }
            
            // Set session date to earliest shot time or current time if no valid shots
            if (earliestShotTime != null) {
                session.setSessionDate(earliestShotTime);
            } else {
                session.setSessionDate(LocalDateTime.now());
            }
        }
        
        // Save only if we have at least one valid shot
        if (session.getShots().isEmpty()) {
            throw new IllegalArgumentException("No valid shots found in the CSV file");
        }
        
        return sessionRepository.save(session);
    }
    
    /**
     * Parse a single shot from Awesome Golf CSV data
     */
    private Shot parseAwesomeGolfShot(List<String> headers, String[] values) {
        Shot shot = new Shot();
        
        // Parse shot date/time from the first column
        String dateTimeStr = values[0].trim();
        if (!dateTimeStr.isEmpty()) {
            try {
                // Awesome Golf format is likely YYYY-MM-DD HH:MM:SS
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                shot.setShotTime(LocalDateTime.parse(dateTimeStr, formatter));
            } catch (DateTimeParseException e) {
                logger.warn("Failed to parse shot time: " + dateTimeStr);
                // Continue with the shot, without the timestamp
            }
        }
        
        // Map values to shot object based on header names
        for (int i = 0; i < headers.size(); i++) {
            if (i >= values.length) break;
            
            String header = headers.get(i).toLowerCase();
            String value = values[i].trim();
            
            // Skip empty values
            if (value.isEmpty()) continue;
            
            try {
                switch (header) {
                    case "club type":
                        shot.setClub(sanitizeInput(value));
                        break;
                    case "club description":
                        shot.setClubDescription(sanitizeInput(value));
                        break;
                    case "altitude":
                    case "altitude [ft]":
                        shot.setAltitude(parseDouble(value));
                        break;
                    case "club speed":
                    case "club speed [mph]":
                        shot.setClubHeadSpeed(parseDouble(value));
                        break;
                    case "ball speed":
                    case "ball speed [mph]":
                        shot.setBallSpeed(parseDouble(value));
                        break;
                    case "carry distance":
                    case "carry distance [yd]":
                        shot.setCarryDistance(parseDouble(value));
                        break;
                    case "total distance":
                    case "total distance [yd]":
                        shot.setTotalDistance(parseDouble(value));
                        break;
                    case "roll distance":
                    case "roll distance [yd]":
                        shot.setRollDistance(parseDouble(value));
                        break;
                    case "smash":
                        shot.setSmash(parseDouble(value));
                        break;
                    case "vertical launch":
                    case "vertical launch [deg]":
                        shot.setLaunchAngle(parseDouble(value));
                        break;
                    case "peak height":
                    case "peak height [ft]":
                        shot.setPeakHeight(parseDouble(value));
                        break;
                    case "descent angle":
                    case "descent angle [deg]":
                        shot.setDescentAngle(parseDouble(value));
                        break;
                    case "horizontal launch":
                    case "horizontal launch [deg]":
                        shot.setHorizontalLaunch(parseDouble(value));
                        shot.setLaunchDirection(parseDouble(value)); // Map to existing field as well
                        break;
                    case "carry lateral distance":
                    case "carry lateral distance [yd]":
                        shot.setCarryLateralDistance(parseDouble(value));
                        break;
                    case "total lateral distance":
                    case "total lateral distance [yd]":
                        shot.setTotalLateralDistance(parseDouble(value));
                        break;
                    case "carry curve distance":
                    case "carry curve distance [yd]":
                        shot.setCarryCurveDistance(parseDouble(value));
                        break;
                    case "total curve distance":
                    case "total curve distance [yd]":
                        shot.setTotalCurveDistance(parseDouble(value));
                        break;
                    case "attack angle":
                    case "attack angle [deg]":
                        shot.setAttackAngle(parseDouble(value));
                        break;
                    case "dynamic loft":
                    case "dynamic loft [deg]":
                        shot.setDynamicLoft(parseDouble(value));
                        break;
                    case "spin loft":
                    case "spin loft [deg]":
                        shot.setSpinLoft(parseDouble(value));
                        break;
                    case "spin rate":
                    case "spin rate [rpm]":
                        shot.setSpinRate(parseDouble(value));
                        break;
                    case "spin axis":
                    case "spin axis [deg]":
                        shot.setSpinAxis(parseDouble(value));
                        break;
                    case "low point":
                    case "low point [in]":
                        shot.setLowPoint(parseDouble(value));
                        break;
                    case "club path":
                    case "club path [deg]":
                        shot.setSwingPath(parseDouble(value));
                        break;
                    case "face path":
                    case "face path [deg]":
                        shot.setFaceToPath(parseDouble(value));
                        break;
                    case "face target":
                    case "face target [deg]":
                        shot.setFaceAngle(parseDouble(value));
                        shot.setFaceTarget(parseDouble(value));
                        break;
                    case "swing plane tilt":
                    case "swing plane tilt [deg]":
                        shot.setSwingPlaneTilt(parseDouble(value));
                        break;
                    case "swing plane rotation":
                    case "swing plane rotation [deg]":
                        shot.setSwingPlaneRotation(parseDouble(value));
                        break;
                    case "shot classification":
                        shot.setShotClassification(sanitizeInput(value));
                        break;
                }
            } catch (NumberFormatException e) {
                logger.warn("Error parsing value for " + header + ": " + value);
            }
        }
        
        return shot;
    }
}