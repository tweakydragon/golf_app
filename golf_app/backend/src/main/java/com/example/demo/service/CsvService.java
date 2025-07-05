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
            
            // Read the first line (header descriptions)
            if ((line = reader.readLine()) != null) {
                String[] headers = line.split(",");
                for (String header : headers) {
                    headerColumns.add(header.trim());
                }
            }
            
            // Skip the second line (units row)
            if ((line = reader.readLine()) != null) {
                // Second line contains units, we'll skip it
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
        
        // Map values to shot object based on column positions (Awesome Golf format)
        // Columns: Date,Club Type,Club Description,Altitude,Club Speed,Ball Speed,Carry Distance,Total Distance,Roll Distance,Smash,Vertical Launch,Peak Height,Descent Angle,Horizontal Launch,Carry Lateral Distance,Total Lateral Distance,Carry Curve Distance,Total Curve Distance,Attack Angle,Dynamic Loft,Spin Loft,Spin Rate,Spin Axis,Spin Reading,Low Point,Club Path,Face Path,Face Target,Swing Plane Tilt,Swing Plane Rotation,Shot Classification
        
        try {
            // Column 1: Club Type
            if (values.length > 1 && !values[1].trim().isEmpty()) {
                shot.setClub(sanitizeInput(values[1].trim()));
            }
            
            // Column 2: Club Description  
            if (values.length > 2 && !values[2].trim().isEmpty()) {
                shot.setClubDescription(sanitizeInput(values[2].trim()));
            }
            
            // Column 3: Altitude
            if (values.length > 3 && !values[3].trim().isEmpty()) {
                shot.setAltitude(parseDouble(values[3].trim()));
            }
            
            // Column 4: Club Speed
            if (values.length > 4 && !values[4].trim().isEmpty()) {
                shot.setClubHeadSpeed(parseDouble(values[4].trim()));
            }
            
            // Column 5: Ball Speed
            if (values.length > 5 && !values[5].trim().isEmpty()) {
                shot.setBallSpeed(parseDouble(values[5].trim()));
            }
            
            // Column 6: Carry Distance
            if (values.length > 6 && !values[6].trim().isEmpty()) {
                shot.setCarryDistance(parseDouble(values[6].trim()));
            }
            
            // Column 7: Total Distance
            if (values.length > 7 && !values[7].trim().isEmpty()) {
                shot.setTotalDistance(parseDouble(values[7].trim()));
            }
            
            // Column 8: Roll Distance
            if (values.length > 8 && !values[8].trim().isEmpty()) {
                shot.setRollDistance(parseDouble(values[8].trim()));
            }
            
            // Column 9: Smash
            if (values.length > 9 && !values[9].trim().isEmpty()) {
                shot.setSmash(parseDouble(values[9].trim()));
            }
            
            // Column 10: Vertical Launch
            if (values.length > 10 && !values[10].trim().isEmpty()) {
                shot.setLaunchAngle(parseDouble(values[10].trim()));
            }
            
            // Column 11: Peak Height
            if (values.length > 11 && !values[11].trim().isEmpty()) {
                shot.setPeakHeight(parseDouble(values[11].trim()));
            }
            
            // Column 12: Descent Angle
            if (values.length > 12 && !values[12].trim().isEmpty()) {
                shot.setDescentAngle(parseDouble(values[12].trim()));
            }
            
            // Column 13: Horizontal Launch
            if (values.length > 13 && !values[13].trim().isEmpty()) {
                Double horizontalLaunch = parseDouble(values[13].trim());
                shot.setHorizontalLaunch(horizontalLaunch);
                shot.setLaunchDirection(horizontalLaunch);
            }
            
            // Column 14: Carry Lateral Distance
            if (values.length > 14 && !values[14].trim().isEmpty()) {
                shot.setCarryLateralDistance(parseDouble(values[14].trim()));
            }
            
            // Column 15: Total Lateral Distance
            if (values.length > 15 && !values[15].trim().isEmpty()) {
                shot.setTotalLateralDistance(parseDouble(values[15].trim()));
            }
            
            // Column 16: Carry Curve Distance
            if (values.length > 16 && !values[16].trim().isEmpty()) {
                shot.setCarryCurveDistance(parseDouble(values[16].trim()));
            }
            
            // Column 17: Total Curve Distance
            if (values.length > 17 && !values[17].trim().isEmpty()) {
                shot.setTotalCurveDistance(parseDouble(values[17].trim()));
            }
            
            // Column 18: Attack Angle
            if (values.length > 18 && !values[18].trim().isEmpty()) {
                shot.setAttackAngle(parseDouble(values[18].trim()));
            }
            
            // Column 19: Dynamic Loft
            if (values.length > 19 && !values[19].trim().isEmpty()) {
                shot.setDynamicLoft(parseDouble(values[19].trim()));
            }
            
            // Column 20: Spin Loft
            if (values.length > 20 && !values[20].trim().isEmpty()) {
                shot.setSpinLoft(parseDouble(values[20].trim()));
            }
            
            // Column 21: Spin Rate
            if (values.length > 21 && !values[21].trim().isEmpty()) {
                shot.setSpinRate(parseDouble(values[21].trim()));
            }
            
            // Column 22: Spin Axis
            if (values.length > 22 && !values[22].trim().isEmpty()) {
                shot.setSpinAxis(parseDouble(values[22].trim()));
            }
            
            // Column 23: Spin Reading (text field - skip)
            
            // Column 24: Low Point
            if (values.length > 24 && !values[24].trim().isEmpty()) {
                shot.setLowPoint(parseDouble(values[24].trim()));
            }
            
            // Column 25: Club Path
            if (values.length > 25 && !values[25].trim().isEmpty()) {
                shot.setSwingPath(parseDouble(values[25].trim()));
            }
            
            // Column 26: Face Path
            if (values.length > 26 && !values[26].trim().isEmpty()) {
                shot.setFaceToPath(parseDouble(values[26].trim()));
            }
            
            // Column 27: Face Target
            if (values.length > 27 && !values[27].trim().isEmpty()) {
                Double faceTarget = parseDouble(values[27].trim());
                shot.setFaceAngle(faceTarget);
                shot.setFaceTarget(faceTarget);
            }
            
            // Column 28: Swing Plane Tilt
            if (values.length > 28 && !values[28].trim().isEmpty()) {
                shot.setSwingPlaneTilt(parseDouble(values[28].trim()));
            }
            
            // Column 29: Swing Plane Rotation
            if (values.length > 29 && !values[29].trim().isEmpty()) {
                shot.setSwingPlaneRotation(parseDouble(values[29].trim()));
            }
            
            // Column 30: Shot Classification
            if (values.length > 30 && !values[30].trim().isEmpty()) {
                shot.setShotClassification(sanitizeInput(values[30].trim()));
            }
            
        } catch (NumberFormatException e) {
            logger.warn("Error parsing numeric value in Awesome Golf shot: " + e.getMessage());
        }
        
        return shot;
    }
}