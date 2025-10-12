package com.enlighten.paging;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class TelemetryRecord {

    // parsed fields
    public final Instant timestamp;
    public final int satelliteId;
    public final int redHigh;
    public final int yellowHigh;
    public final int yellowLow;
    public final int redLow;
    public final int rawValue;
    public final String component;  // "BATT" or "TSTAT"

    // Timestamp format in input (assume UTC)
    private static final DateTimeFormatter INPUT_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS");

    public TelemetryRecord(Instant timestamp,
                           int satelliteId,
                           int redHigh,
                           int yellowHigh,
                           int yellowLow,
                           int redLow,
                           int rawValue,
                           String component) {
        this.timestamp  = timestamp;
        this.satelliteId = satelliteId;
        this.redHigh     = redHigh;
        this.yellowHigh  = yellowHigh;
        this.yellowLow   = yellowLow;
        this.redLow      = redLow;
        this.rawValue    = rawValue;
        this.component   = component;
    }


     // Parse one pipe delimited line into a TelemetryRecord
     // Handles inline // comments and blank lines

     // @return TelemetryRecord, or null if the line is blank after stripping comments
     // @throws IllegalArgumentException if the non-blank line is malformed

    public static TelemetryRecord fromLine(String line) {
        if (line == null) return null;

        // Strip inline comments and trim
        int comment = line.indexOf("//");
        if (comment >= 0) line = line.substring(0, comment);
        line = line.trim();

        // Skip blanks
        if (line.isEmpty()) return null;

        // Split on pipes
        String[] p = line.split("\\|");
        if (p.length < 8) {
            throw new IllegalArgumentException("Bad telemetry line (need 8 fields): " + line);
        }

        // Parse timestamp in UTC
        LocalDateTime ldt = LocalDateTime.parse(p[0].trim(), INPUT_FMT);
        Instant ts = ldt.toInstant(ZoneOffset.UTC);

        int satId  = Integer.parseInt(p[1].trim());
        int rHigh  = Integer.parseInt(p[2].trim());
        int yHigh  = Integer.parseInt(p[3].trim());
        int yLow   = Integer.parseInt(p[4].trim());
        int rLow   = Integer.parseInt(p[5].trim());
        int raw    = Integer.parseInt(p[6].trim());
        String comp = p[7].trim();  // "BATT" or "TSTAT"

        return new TelemetryRecord(ts, satId, rHigh, yHigh, yLow, rLow, raw, comp);
    }

    // Violation helpers

    public boolean isBattViolation() {
        // Battery alert when voltage is dangerously low
        return "BATT".equals(component) && rawValue < redLow;
    }

    public boolean isTstatViolation() {
        // Thermostat alert when temperature is dangerously high
        return "TSTAT".equals(component) && rawValue > redHigh;
    }

    // Tracks if there is any violation? (used by the sliding window engine)
    public boolean isViolation() {
        return isBattViolation() || isTstatViolation();
    }

    // Map the violation to the required severity string
    public String violationSeverity() {
        if (isBattViolation())  return "RED LOW";
        if (isTstatViolation()) return "RED HIGH";
        return null;
    }

    @Override
    public String toString() {
        return "TelemetryRecord{" +
                "ts=" + timestamp +
                ", sat=" + satelliteId +
                ", raw=" + rawValue +
                ", comp='" + component + '\'' +
                '}';
    }
}