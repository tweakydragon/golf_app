package com.example.demo.parsing;

/** Utility to normalize CSV headers for mapping. */
public final class HeaderNormalizer {
    private HeaderNormalizer() {}

    public static String normalize(String raw) {
        if (raw == null) return "";
        // lowercase, trim, collapse internal whitespace to single space
        String cleaned = raw.trim().toLowerCase().replaceAll("\\s+", " ");
        // remove surrounding quotes if present
        if ((cleaned.startsWith("\"") && cleaned.endsWith("\"")) || (cleaned.startsWith("'") && cleaned.endsWith("'"))) {
            cleaned = cleaned.substring(1, cleaned.length()-1);
        }
        return cleaned;
    }
}
