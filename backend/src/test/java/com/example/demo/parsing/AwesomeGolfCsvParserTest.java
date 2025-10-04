package com.example.demo.parsing;

import com.example.demo.model.Shot;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class AwesomeGolfCsvParserTest {

    @Test
    void parsesSampleAwesomeGolfCsv() throws Exception {
        InputStream is = getClass().getResourceAsStream("/csv/awesome_golf/sample_awesome_golf.csv");
        assertNotNull(is, "Sample Awesome Golf CSV fixture not found");
        AwesomeGolfCsvParser parser = new AwesomeGolfCsvParser();
        ParseResult result = parser.parse(is);
        assertEquals(2, result.getShots().size());
        assertEquals(2, result.getTotalRows());
        assertEquals(0, result.getSkippedRows());
        assertNotNull(result.getEarliestShotTimestamp());
        Shot first = result.getShots().get(0);
        assertEquals(150.0, first.getBallSpeed(), 0.0001);
        assertEquals(105.0, first.getClubHeadSpeed(), 0.0001);
        assertEquals(250.0, first.getCarryDistance(), 0.0001);
        assertEquals(270.0, first.getTotalDistance(), 0.0001);
        assertEquals(13.0, first.getLaunchAngle(), 0.0001);
    }
}
