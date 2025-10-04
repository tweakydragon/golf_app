package com.example.demo.parsing;

import com.example.demo.model.Shot;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Result summary of parsing a CSV import. */
public class ParseResult {
    private final List<Shot> shots;
    private final int totalRows;
    private final int skippedRows;
    private final Map<String,Integer> fieldErrorCounts;
    private final String earliestShotTimestamp; // ISO string or null

    public ParseResult(List<Shot> shots, int totalRows, int skippedRows,
                       Map<String,Integer> fieldErrorCounts, String earliestShotTimestamp) {
        this.shots = shots;
        this.totalRows = totalRows;
        this.skippedRows = skippedRows;
        this.fieldErrorCounts = fieldErrorCounts;
        this.earliestShotTimestamp = earliestShotTimestamp;
    }

    public List<Shot> getShots() { return shots; }
    public int getTotalRows() { return totalRows; }
    public int getSkippedRows() { return skippedRows; }
    public Map<String,Integer> getFieldErrorCounts() { return Collections.unmodifiableMap(fieldErrorCounts); }
    public String getEarliestShotTimestamp() { return earliestShotTimestamp; }
}
