package com.example.demo.parsing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SafeNumberParserTest {
    @Test
    void parsesInteger() {
        assertEquals(123, SafeNumberParser.toInteger("123"));
        assertNull(SafeNumberParser.toInteger("abc"));
    }
    @Test
    void parsesDouble() {
        assertEquals(12.34, SafeNumberParser.toDouble("12.34"));
        assertEquals(-5.0, SafeNumberParser.toDouble("-5"));
        assertNull(SafeNumberParser.toDouble(""));
        assertNull(SafeNumberParser.toDouble("xyz"));
    }
}
