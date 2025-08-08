package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Pagination information for API responses
 */
@Schema(description = "Pagination information")
public class PaginationInfo {
    
    @JsonProperty("page")
    @Schema(description = "Current page number (0-based)", example = "0")
    private int page;
    
    @JsonProperty("size")
    @Schema(description = "Number of items per page", example = "20")
    private int size;
    
    @JsonProperty("totalElements")
    @Schema(description = "Total number of elements", example = "150")
    private long totalElements;
    
    @JsonProperty("totalPages")
    @Schema(description = "Total number of pages", example = "8")
    private int totalPages;
    
    @JsonProperty("first")
    @Schema(description = "Whether this is the first page", example = "true")
    private boolean first;
    
    @JsonProperty("last")
    @Schema(description = "Whether this is the last page", example = "false")
    private boolean last;
    
    @JsonProperty("hasNext")
    @Schema(description = "Whether there is a next page", example = "true")
    private boolean hasNext;
    
    @JsonProperty("hasPrevious")
    @Schema(description = "Whether there is a previous page", example = "false")
    private boolean hasPrevious;
    
    // Constructors
    public PaginationInfo() {}
    
    public PaginationInfo(int page, int size, long totalElements, int totalPages) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = page == 0;
        this.last = page == totalPages - 1;
        this.hasNext = page < totalPages - 1;
        this.hasPrevious = page > 0;
    }
    
    // Getters and Setters
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public long getTotalElements() {
        return totalElements;
    }
    
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    
    public boolean isFirst() {
        return first;
    }
    
    public void setFirst(boolean first) {
        this.first = first;
    }
    
    public boolean isLast() {
        return last;
    }
    
    public void setLast(boolean last) {
        this.last = last;
    }
    
    public boolean isHasNext() {
        return hasNext;
    }
    
    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
    
    public boolean isHasPrevious() {
        return hasPrevious;
    }
    
    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
}