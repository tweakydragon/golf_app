package com.example.demo.parsing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HeaderNormalizerTest {
    @Test
    void normalizesBasicCases() {
        assertEquals("ball speed", HeaderNormalizer.normalize("Ball  Speed"));
    // Units text like (deg) is preserved intentionally
    assertEquals("launch angle (deg)", HeaderNormalizer.normalize(" Launch Angle (deg) "));
    assertEquals("spin rate (rpm)", HeaderNormalizer.normalize("Spin  Rate (RPM)"));
    }
}
