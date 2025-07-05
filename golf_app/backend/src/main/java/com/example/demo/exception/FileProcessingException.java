package com.example.demo.exception;

/**
 * Exception thrown when file processing fails
 */
public class FileProcessingException extends BusinessException {
    
    private static final String ERROR_CODE = "FILE_PROCESSING_ERROR";
    
    public FileProcessingException(String message) {
        super(message, ERROR_CODE);
    }
    
    public FileProcessingException(String message, Throwable cause) {
        super(message, ERROR_CODE, cause);
    }
    
    public FileProcessingException(String fileName, String operation, Throwable cause) {
        super(String.format("Failed to %s file: %s", operation, fileName), ERROR_CODE, cause);
    }
}