package com.example.demo.parsing;

import com.example.demo.model.Shot;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class GarminCsvParserTest {

    @Test
    void parsesSampleGarminCsv() throws Exception {
        InputStream is = getClass().getResourceAsStream("/csv/garmin/sample_garmin.csv");
        assertNotNull(is, "Sample Garmin CSV fixture not found");
        GarminCsvParser parser = new GarminCsvParser();
        ParseResult result = parser.parse(is);
        assertEquals(2, result.getShots().size(), "Expected two parsed shots");
        assertEquals(2, result.getTotalRows());
        assertEquals(0, result.getSkippedRows());
        Shot first = result.getShots().get(0);
        assertEquals(145.2, first.getBallSpeed(), 0.0001);
        assertEquals(103.5, first.getClubHeadSpeed(), 0.0001);
        assertEquals(12.5, first.getLaunchAngle(), 0.0001);
        assertEquals(245.8, first.getCarryDistance(), 0.0001);
        assertEquals(267.2, first.getTotalDistance(), 0.0001);
        assertNull(result.getEarliestShotTimestamp(), "No timestamp expected for Garmin sample");
    }
}
