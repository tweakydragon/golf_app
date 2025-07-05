package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Standardized API response wrapper
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {
    
    @JsonProperty("success")
    @Schema(description = "Indicates if the request was successful", example = "true")
    private boolean success;
    
    @JsonProperty("data")
    @Schema(description = "The response data")
    private T data;
    
    @JsonProperty("message")
    @Schema(description = "Response message", example = "Operation completed successfully")
    private String message;
    
    @JsonProperty("errorCode")
    @Schema(description = "Error code when request fails", example = "RESOURCE_NOT_FOUND")
    private String errorCode;
    
    @JsonProperty("errors")
    @Schema(description = "List of validation errors")
    private List<String> errors;
    
    @JsonProperty("fieldErrors")
    @Schema(description = "Field-specific validation errors")
    private Map<String, String> fieldErrors;
    
    @JsonProperty("timestamp")
    @Schema(description = "Response timestamp")
    private LocalDateTime timestamp;
    
    @JsonProperty("path")
    @Schema(description = "Request path", example = "/api/sessions")
    private String path;
    
    @JsonProperty("pagination")
    @Schema(description = "Pagination information")
    private PaginationInfo pagination;
    
    // Constructors
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ApiResponse(boolean success, T data, String message) {
        this();
        this.success = success;
        this.data = data;
        this.message = message;
    }
    
    // Success response builders
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "Operation completed successfully");
    }
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }
    
    public static <T> ApiResponse<T> success(T data, String message, PaginationInfo pagination) {
        ApiResponse<T> response = new ApiResponse<>(true, data, message);
        response.setPagination(pagination);
        return response;
    }
    
    // Error response builders
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message);
    }
    
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        ApiResponse<T> response = new ApiResponse<>(false, null, message);
        response.setErrorCode(errorCode);
        return response;
    }
    
    public static <T> ApiResponse<T> error(String message, String errorCode, List<String> errors) {
        ApiResponse<T> response = new ApiResponse<>(false, null, message);
        response.setErrorCode(errorCode);
        response.setErrors(errors);
        return response;
    }
    
    public static <T> ApiResponse<T> validationError(String message, Map<String, String> fieldErrors) {
        ApiResponse<T> response = new ApiResponse<>(false, null, message);
        response.setErrorCode("VALIDATION_FAILED");
        response.setFieldErrors(fieldErrors);
        return response;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
    
    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public PaginationInfo getPagination() {
        return pagination;
    }
    
    public void setPagination(PaginationInfo pagination) {
        this.pagination = pagination;
    }
}