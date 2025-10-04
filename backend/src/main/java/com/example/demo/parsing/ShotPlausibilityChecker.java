package com.example.demo.parsing;

import com.example.demo.model.Shot;

/** Simple plausibility filters for obviously bad numeric values. */
public final class ShotPlausibilityChecker {
    private ShotPlausibilityChecker() {}

    public static boolean isPlausible(Shot s) {
        return within(s.getBallSpeed(), 0, 250) &&
               within(s.getClubHeadSpeed(), 0, 200) &&
               within(s.getLaunchAngle(), -10, 90) &&
               within(s.getCarryDistance(), 0, 450) &&
               within(s.getTotalDistance(), 0, 550) &&
               within(s.getSpinRate(), 0, 12000) &&
               within(s.getSpinAxis(), -90, 90);
    }

    private static boolean within(Double v, double min, double max) {
        if (v == null) return true; // absence not implausible
        return v >= min && v <= max;
    }
}
