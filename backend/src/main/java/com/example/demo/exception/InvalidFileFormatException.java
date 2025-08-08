package com.example.demo.exception;

/**
 * Exception thrown when an uploaded file has an invalid format
 */
public class InvalidFileFormatException extends BusinessException {
    
    private static final String ERROR_CODE = "INVALID_FILE_FORMAT";
    
    public InvalidFileFormatException(String message) {
        super(message, ERROR_CODE);
    }
    
    public InvalidFileFormatException(String expectedFormat, String actualFormat) {
        super(String.format("Expected file format: %s, but received: %s", expectedFormat, actualFormat), ERROR_CODE);
    }
    
    public InvalidFileFormatException(String message, Throwable cause) {
        super(message, ERROR_CODE, cause);
    }
}