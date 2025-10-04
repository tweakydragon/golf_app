package com.example.demo.service;

import com.example.demo.exception.InvalidFileFormatException;
import com.example.demo.exception.ValidationException;
import com.example.demo.model.Session;
import com.example.demo.model.DataSource;
import com.example.demo.repository.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
// Removed unused imports after parser refactor

import com.example.demo.parsing.GarminCsvParser;
import com.example.demo.parsing.ParseResult;
import com.example.demo.parsing.ShotPlausibilityChecker;
import com.example.demo.parsing.AwesomeGolfCsvParser;

@Service
public class CsvService {
    private static final Logger logger = LoggerFactory.getLogger(CsvService.class);
    @Autowired
    private SessionRepository sessionRepository;

    public Session processGarminR10Csv(MultipartFile file, String title, String location) throws IOException {
        if (file.isEmpty()) throw new InvalidFileFormatException("File is empty");
        if (!isValidCsvFile(file)) throw new InvalidFileFormatException("Invalid file format. Please upload a CSV file");

        // Normalize inputs (no HTML escaping here)
        String sanitizedTitle = safeTitle(title);
        String sanitizedLocation = safeLocation(location);
        if (sanitizedTitle.isEmpty()) throw new ValidationException("Title cannot be empty");

        Session session = new Session(sanitizedTitle);
        session.setLocation(sanitizedLocation);
        session.setUploadDate(LocalDateTime.now());

        GarminCsvParser parser = new GarminCsvParser();
        ParseResult result = parser.parse(file.getInputStream());
        session.setSourceType(DataSource.GARMIN_R10);
        long[] filtered = {0};
        result.getShots().forEach(sh -> {
            if (ShotPlausibilityChecker.isPlausible(sh)) session.addShot(sh); else filtered[0]++; });

        if (logger.isInfoEnabled()) {
            logger.info(String.format("garmin_import title=%s rows=%d shots=%d skipped=%d filtered=%d fieldErrors=%d",
                sanitizedTitle,
                result.getTotalRows(),
                result.getShots().size(),
                result.getSkippedRows(),
                filtered[0],
                result.getFieldErrorCounts().values().stream().mapToInt(Integer::intValue).sum()));
        }
        if (logger.isDebugEnabled() && !result.getFieldErrorCounts().isEmpty()) {
            logger.debug("garmin_field_error_counts=" + result.getFieldErrorCounts());
        }

        if (session.getShots().isEmpty()) throw new ValidationException("No valid shots found in the CSV file");
        if (session.getSessionDate() == null) session.setSessionDate(LocalDateTime.now());
        return sessionRepository.save(session);
    }

    private boolean isValidCsvFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) return false;
        String contentType = file.getContentType();
        return contentType != null && (
            contentType.equals("text/csv") ||
            contentType.equals("application/vnd.ms-excel") ||
            contentType.equals("application/csv") ||
            contentType.equals("text/plain")
        );
    }

    // Basic normalization and length cap only; escaping deferred to rendering
    private String safeTitle(String input) {
        if (input == null) return "";
        String t = input.trim();
        return t.length() > 255 ? t.substring(0,255) : t;
    }
    private String safeLocation(String input) {
        if (input == null) return "";
        String t = input.trim();
        return t.length() > 255 ? t.substring(0,255) : t;
    }

    public Session processAwesomeGolfCsv(MultipartFile file, String title, String location) throws IOException {
        if (file.isEmpty()) throw new InvalidFileFormatException("File is empty");
        if (!isValidCsvFile(file)) throw new InvalidFileFormatException("Invalid file format. Please upload a CSV file");

        String sanitizedTitle = safeTitle(title);
        String sanitizedLocation = safeLocation(location);
        if (sanitizedTitle.isEmpty()) throw new ValidationException("Title cannot be empty");

        Session session = new Session(sanitizedTitle);
        session.setLocation(sanitizedLocation);
        session.setUploadDate(LocalDateTime.now());
        session.setSourceType(DataSource.AWESOME_GOLF);

        AwesomeGolfCsvParser parser = new AwesomeGolfCsvParser();
        ParseResult parseResult = parser.parse(file.getInputStream());
        final int[] shotCounter = {1};
    long[] filtered = {0};
    parseResult.getShots().forEach(s -> { s.setShotNumber(shotCounter[0]++); if (ShotPlausibilityChecker.isPlausible(s)) session.addShot(s); else filtered[0]++; });
        if (session.getSessionDate() == null) {
            if (parseResult.getEarliestShotTimestamp() != null) {
                try { session.setSessionDate(LocalDateTime.parse(parseResult.getEarliestShotTimestamp())); } catch (Exception ignored) { session.setSessionDate(LocalDateTime.now()); }
            } else {
                session.setSessionDate(LocalDateTime.now());
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info(String.format("awesome_golf_import title=%s rows=%d shots=%d skipped=%d filtered=%d fieldErrors=%d",
                sanitizedTitle,
                parseResult.getTotalRows(),
                parseResult.getShots().size(),
                parseResult.getSkippedRows(),
                filtered[0],
                parseResult.getFieldErrorCounts().values().stream().mapToInt(Integer::intValue).sum()));
        }
        if (logger.isDebugEnabled() && !parseResult.getFieldErrorCounts().isEmpty()) {
            logger.debug("awesome_golf_field_error_counts=" + parseResult.getFieldErrorCounts());
        }
        if (session.getShots().isEmpty()) throw new ValidationException("No valid shots found in the CSV file");
        return sessionRepository.save(session);
    }
}