package com.enlighten.paging;

import java.time.Instant;

// Plain object that will be serialized to JSON by Gson
public class Alert {
    public final int satelliteId;
    public final String severity;   // "RED LOW" or "RED HIGH"
    public final String component;  // "BATT" or "TSTAT"
    public final Instant timestamp; // when the 3rd violation happened

    public Alert(int satelliteId, String severity, String component, Instant timestamp) {
        this.satelliteId = satelliteId;
        this.severity = severity;
        this.component = component;
        this.timestamp = timestamp;
    }

    public static Alert from(TelemetryRecord r, String severity) {
        return new Alert(r.satelliteId, severity, r.component, r.timestamp);
    }
}