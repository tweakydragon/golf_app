package com.example.demo.parsing;

import com.example.demo.model.Shot;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

/** Parser for Garmin R10 CSV files using header-driven mapping. */
public class GarminCsvParser {
    // Logger reserved for future detailed debug; intentionally omitted for now to avoid unused warning.

    private final Map<String, BiConsumerWithErrors<Shot,String>> headerMapping;

    @FunctionalInterface
    public interface BiConsumerWithErrors<T, U> {
        void accept(T t, U u, Map<String,Integer> errorCounts);
    }

    public GarminCsvParser() {
        Map<String, BiConsumerWithErrors<Shot,String>> map = new HashMap<>();
        map.put("shot", (s,v,ec) -> setInt("shot", v, ec, val -> s.setShotNumber(val)));
        map.put("shot number", (s,v,ec) -> setInt("shot number", v, ec, val -> s.setShotNumber(val)));
        map.put("club", (s,v,ec) -> s.setClub(v));
        map.put("ball speed", (s,v,ec) -> setDouble("ball speed", v, ec, s::setBallSpeed));
        map.put("ball speed (mph)", (s,v,ec) -> setDouble("ball speed", v, ec, s::setBallSpeed));
        map.put("club head speed", (s,v,ec) -> setDouble("club head speed", v, ec, s::setClubHeadSpeed));
        map.put("club speed", (s,v,ec) -> setDouble("club head speed", v, ec, s::setClubHeadSpeed));
        map.put("club speed (mph)", (s,v,ec) -> setDouble("club head speed", v, ec, s::setClubHeadSpeed));
        map.put("launch angle", (s,v,ec) -> setDouble("launch angle", v, ec, s::setLaunchAngle));
        map.put("launch angle (deg)", (s,v,ec) -> setDouble("launch angle", v, ec, s::setLaunchAngle));
        map.put("launch direction", (s,v,ec) -> setDouble("launch direction", v, ec, s::setLaunchDirection));
        map.put("launch direction (deg)", (s,v,ec) -> setDouble("launch direction", v, ec, s::setLaunchDirection));
        map.put("spin rate", (s,v,ec) -> setDouble("spin rate", v, ec, s::setSpinRate));
        map.put("spin rate (rpm)", (s,v,ec) -> setDouble("spin rate", v, ec, s::setSpinRate));
        map.put("spin axis", (s,v,ec) -> setDouble("spin axis", v, ec, s::setSpinAxis));
        map.put("spin axis (deg)", (s,v,ec) -> setDouble("spin axis", v, ec, s::setSpinAxis));
        map.put("carry", (s,v,ec) -> setDouble("carry distance", v, ec, s::setCarryDistance));
        map.put("carry distance", (s,v,ec) -> setDouble("carry distance", v, ec, s::setCarryDistance));
        map.put("carry distance (yards)", (s,v,ec) -> setDouble("carry distance", v, ec, s::setCarryDistance));
        map.put("total", (s,v,ec) -> setDouble("total distance", v, ec, s::setTotalDistance));
        map.put("total distance", (s,v,ec) -> setDouble("total distance", v, ec, s::setTotalDistance));
        map.put("total distance (yards)", (s,v,ec) -> setDouble("total distance", v, ec, s::setTotalDistance));
        map.put("deviation", (s,v,ec) -> setDouble("deviation", v, ec, s::setDeviation));
        map.put("deviation (ft)", (s,v,ec) -> setDouble("deviation", v, ec, s::setDeviation));
        map.put("apex", (s,v,ec) -> setDouble("apex", v, ec, s::setApex));
        map.put("apex (ft)", (s,v,ec) -> setDouble("apex", v, ec, s::setApex));
        map.put("attack angle", (s,v,ec) -> setDouble("attack angle", v, ec, s::setAttackAngle));
        map.put("attack angle (deg)", (s,v,ec) -> setDouble("attack angle", v, ec, s::setAttackAngle));
        map.put("face angle", (s,v,ec) -> setDouble("face angle", v, ec, s::setFaceAngle));
        map.put("face angle (deg)", (s,v,ec) -> setDouble("face angle", v, ec, s::setFaceAngle));
        map.put("face to path", (s,v,ec) -> setDouble("face to path", v, ec, s::setFaceToPath));
        map.put("face to path (deg)", (s,v,ec) -> setDouble("face to path", v, ec, s::setFaceToPath));
        map.put("swing path", (s,v,ec) -> setDouble("swing path", v, ec, s::setSwingPath));
        map.put("path", (s,v,ec) -> setDouble("swing path", v, ec, s::setSwingPath));
        map.put("path (deg)", (s,v,ec) -> setDouble("swing path", v, ec, s::setSwingPath));
        map.put("swing plane", (s,v,ec) -> setDouble("swing plane", v, ec, s::setSwingPlane));
        map.put("plane", (s,v,ec) -> setDouble("swing plane", v, ec, s::setSwingPlane));
        map.put("plane (deg)", (s,v,ec) -> setDouble("swing plane", v, ec, s::setSwingPlane));
        map.put("vertical face impact", (s,v,ec) -> setDouble("vertical face impact", v, ec, s::setVerticalFaceImpact));
        map.put("vertical impact (in)", (s,v,ec) -> setDouble("vertical face impact", v, ec, s::setVerticalFaceImpact));
        map.put("horizontal face impact", (s,v,ec) -> setDouble("horizontal face impact", v, ec, s::setHorizontalFaceImpact));
        map.put("horizontal impact (in)", (s,v,ec) -> setDouble("horizontal face impact", v, ec, s::setHorizontalFaceImpact));
        headerMapping = Collections.unmodifiableMap(map);
    }

    private void setDouble(String field, String raw, Map<String,Integer> ec, java.util.function.Consumer<Double> setter) {
        Double d = SafeNumberParser.toDouble(raw);
        if (d == null) {
            ec.merge(field, 1, Integer::sum);
        } else {
            setter.accept(d);
        }
    }

    private void setInt(String field, String raw, Map<String,Integer> ec, java.util.function.Consumer<Integer> setter) {
        Integer i = SafeNumberParser.toInteger(raw);
        if (i == null) {
            ec.merge(field, 1, Integer::sum);
        } else {
            setter.accept(i);
        }
    }

    public ParseResult parse(InputStream in) throws IOException {
        List<Shot> shots = new ArrayList<>();
        Map<String,Integer> errorCounts = new HashMap<>();
        int total = 0; int skipped = 0;
        LocalDateTime earliest = null;

        try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader)) {
            for (CSVRecord record : parser) {
                total++;
                Shot shot = new Shot();
                boolean anyValue = false;
                for (Map.Entry<String,String> e : record.toMap().entrySet()) {
                    String normHeader = HeaderNormalizer.normalize(e.getKey());
                    String value = e.getValue();
                    if (value != null && !value.trim().isEmpty()) anyValue = true;
                    BiConsumerWithErrors<Shot,String> consumer = headerMapping.get(normHeader);
                    if (consumer != null) {
                        consumer.accept(shot, value, errorCounts);
                    }
                }
                if (!anyValue) { skipped++; continue; }
                shots.add(shot);
            }
        }
        return new ParseResult(shots, total, skipped, errorCounts, earliest != null ? earliest.toString() : null);
    }
}
