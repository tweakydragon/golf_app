package com.example.demo.exception;

import java.util.Map;

/**
 * Exception thrown when validation fails
 */
public class ValidationException extends BusinessException {
    
    private static final String ERROR_CODE = "VALIDATION_FAILED";
    private final Map<String, String> fieldErrors;
    
    public ValidationException(String message) {
        super(message, ERROR_CODE);
        this.fieldErrors = null;
    }
    
    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message, ERROR_CODE);
        this.fieldErrors = fieldErrors;
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, ERROR_CODE, cause);
        this.fieldErrors = null;
    }
    
    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}