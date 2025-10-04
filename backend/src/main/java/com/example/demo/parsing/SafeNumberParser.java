package com.example.demo.parsing;

/** Safe numeric parsing helpers returning null on failure, never throwing to callers. */
public final class SafeNumberParser {
    private SafeNumberParser() {}

    public static Double toDouble(String value) {
        if (value == null) return null;
        String v = value.trim();
        if (v.isEmpty()) return null;
        // remove non numeric except . -
    v = v.replaceAll("[^0-9.\\-]", "");
        if (v.isEmpty() || v.equals("-") || v.equals(".")) return null;
        try { return Double.parseDouble(v); } catch (NumberFormatException e) { return null; }
    }

    public static Integer toInteger(String value) {
        if (value == null) return null;
        String v = value.trim();
        if (v.isEmpty()) return null;
    v = v.replaceAll("[^0-9\\-]", "");
        if (v.isEmpty() || v.equals("-")) return null;
        try { return Integer.parseInt(v); } catch (NumberFormatException e) { return null; }
    }
}
