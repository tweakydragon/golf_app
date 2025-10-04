package com.example.demo.parsing;

import com.example.demo.model.Shot;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Parser for Awesome Golf CSV files (two header rows: names then units). */
public class AwesomeGolfCsvParser {

    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ParseResult parse(InputStream in) throws IOException {
        List<Shot> shots = new ArrayList<>();
        Map<String,Integer> fieldErrors = new HashMap<>();
        int total = 0; int skipped = 0;
        LocalDateTime earliest = null;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            // Header line
            String headerLine = br.readLine();
            if (headerLine == null) {
                return new ParseResult(shots, total, skipped, fieldErrors, null);
            }
            // Units line (skip)
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                total++;
                // Use a lightweight CSV split respecting simple quotes (fallback to commons-csv later if needed)
                // For now assume no embedded commas inside quoted fields in Awesome Golf export beyond description; handle quotes removal.
                String[] cols = line.split(",", -1);
                if (cols.length < 7) { // minimal meaningful columns check
                    skipped++; continue;
                }
                Shot shot = new Shot();
                boolean anyValue = false;
                // 0 DateTime
                String dt = safeGet(cols,0);
                if (!dt.isEmpty()) {
                    try {
                        LocalDateTime ts = LocalDateTime.parse(dt, TS_FMT);
                        shot.setShotTime(ts);
                        if (earliest == null || ts.isBefore(earliest)) earliest = ts;
                        anyValue = true;
                    } catch (DateTimeParseException e) {
                        fieldErrors.merge("shotTime",1,Integer::sum);
                    }
                }
                // 1 Club Type
                setString(shot::setClub, safeGet(cols,1));
                // 2 Club Description
                setString(shot::setClubDescription, safeGet(cols,2));
                // 3 Altitude
                setDouble(shot::setAltitude, safeGet(cols,3), fieldErrors, "altitude");
                // 4 Club Speed
                setDouble(shot::setClubHeadSpeed, safeGet(cols,4), fieldErrors, "club head speed");
                // 5 Ball Speed
                setDouble(shot::setBallSpeed, safeGet(cols,5), fieldErrors, "ball speed");
                // 6 Carry Distance
                setDouble(shot::setCarryDistance, safeGet(cols,6), fieldErrors, "carry distance");
                // 7 Total Distance
                setDouble(shot::setTotalDistance, safeGet(cols,7), fieldErrors, "total distance");
                // 8 Roll Distance
                setDouble(shot::setRollDistance, safeGet(cols,8), fieldErrors, "roll distance");
                // 9 Smash
                setDouble(shot::setSmash, safeGet(cols,9), fieldErrors, "smash");
                // 10 Vertical Launch => launchAngle
                setDouble(shot::setLaunchAngle, safeGet(cols,10), fieldErrors, "launch angle");
                // 11 Peak Height
                setDouble(shot::setPeakHeight, safeGet(cols,11), fieldErrors, "peak height");
                // 12 Descent Angle
                setDouble(shot::setDescentAngle, safeGet(cols,12), fieldErrors, "descent angle");
                // 13 Horizontal Launch -> launchDirection + horizontalLaunch
                String horiz = safeGet(cols,13);
                Double hVal = SafeNumberParser.toDouble(horiz);
                if (hVal != null) { shot.setLaunchDirection(hVal); shot.setHorizontalLaunch(hVal);} else if(!horiz.isEmpty()) fieldErrors.merge("horizontal launch",1,Integer::sum);
                // 14 Carry Lateral Distance
                setDouble(shot::setCarryLateralDistance, safeGet(cols,14), fieldErrors, "carry lateral distance");
                // 15 Total Lateral Distance
                setDouble(shot::setTotalLateralDistance, safeGet(cols,15), fieldErrors, "total lateral distance");
                // 16 Carry Curve Distance
                setDouble(shot::setCarryCurveDistance, safeGet(cols,16), fieldErrors, "carry curve distance");
                // 17 Total Curve Distance
                setDouble(shot::setTotalCurveDistance, safeGet(cols,17), fieldErrors, "total curve distance");
                // 18 Attack Angle
                setDouble(shot::setAttackAngle, safeGet(cols,18), fieldErrors, "attack angle");
                // 19 Dynamic Loft
                setDouble(shot::setDynamicLoft, safeGet(cols,19), fieldErrors, "dynamic loft");
                // 20 Spin Loft
                setDouble(shot::setSpinLoft, safeGet(cols,20), fieldErrors, "spin loft");
                // 21 Spin Rate
                setDouble(shot::setSpinRate, safeGet(cols,21), fieldErrors, "spin rate");
                // 22 Spin Axis
                setDouble(shot::setSpinAxis, safeGet(cols,22), fieldErrors, "spin axis");
                // 23 Spin Reading (skip textual)
                // 24 Low Point
                setDouble(shot::setLowPoint, safeGet(cols,24), fieldErrors, "low point");
                // 25 Club Path -> swingPath
                setDouble(shot::setSwingPath, safeGet(cols,25), fieldErrors, "swing path");
                // 26 Face Path -> faceToPath
                setDouble(shot::setFaceToPath, safeGet(cols,26), fieldErrors, "face to path");
                // 27 Face Target -> faceAngle & faceTarget
                String faceTarget = safeGet(cols,27);
                Double ftVal = SafeNumberParser.toDouble(faceTarget);
                if (ftVal != null) { shot.setFaceAngle(ftVal); shot.setFaceTarget(ftVal);} else if(!faceTarget.isEmpty()) fieldErrors.merge("face target",1,Integer::sum);
                // 28 Swing Plane Tilt
                setDouble(shot::setSwingPlaneTilt, safeGet(cols,28), fieldErrors, "swing plane tilt");
                // 29 Swing Plane Rotation
                setDouble(shot::setSwingPlaneRotation, safeGet(cols,29), fieldErrors, "swing plane rotation");
                // 30 Shot Classification
                String classification = safeGet(cols,30);
                if (!classification.isEmpty()) shot.setShotClassification(classification);

                if (!anyValue && shot.getBallSpeed() == null && shot.getClubHeadSpeed() == null) { // heuristic for empty row
                    skipped++; continue;
                }
                shots.add(shot);
            }
        }

        return new ParseResult(shots, total, skipped, fieldErrors, earliest != null ? earliest.toString() : null);
    }

    private String safeGet(String[] arr, int idx) { return idx < arr.length ? trimQuotes(arr[idx].trim()) : ""; }
    private String trimQuotes(String s) { if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) return s.substring(1,s.length()-1); return s; }
    private void setString(java.util.function.Consumer<String> setter, String v) { if (!v.isEmpty()) setter.accept(v); }
    private void setDouble(java.util.function.Consumer<Double> setter, String raw, Map<String,Integer> ec, String field) {
        if (raw.isEmpty()) return; Double d = SafeNumberParser.toDouble(raw); if (d==null) ec.merge(field,1,Integer::sum); else setter.accept(d); }
}
