package com.enlighten.paging;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


 // Keeps a 5 minute sliding window of violation timestamps for each (satelliteId, component)
 // Emits an Alert on the 3rd violation within the window
 // Suppression policy A: after emitting, suppress further alerts for that key until
 // the window fully clears (i.e: no timestamps left in the deque)

public class SlidingWindowEngine {

    private static final Duration WINDOW = Duration.ofMinutes(5);

    // For each (satId:component) keep a deque of violation times (most recent at the tail)
    private final Map<String, Deque<Instant>> buckets = new HashMap<>();

    // Keys currently “locked” because we just emitted an alert; unlock when their window clears
    private final Set<String> locked = new HashSet<>();

     // Process one telemetry record. Returns an Alert when the 3rd violation
     // is observed within a 5 minute inclusive window; otherwise returns null

    public Alert process(TelemetryRecord r) {
        // Ignore non-violations fast.
        if (!r.isViolation()) return null;

        final String key = r.satelliteId + ":" + r.component;
        final Instant t = r.timestamp;

        // Get/create the deque for this key.
        Deque<Instant> q = buckets.computeIfAbsent(key, k -> new ArrayDeque<>());

        // Evict anything older than 5 minutes from the head.
        evictOld(q, t);

        // If this key is locked (we recently emitted), only unlock when fully cleared.
        if (locked.contains(key)) {
            if (q.isEmpty()) locked.remove(key);   // window cleared → unlock
            // While locked, we suppress new alerts (still accept/push events below so state keeps moving).
        }

        // Record this violation time.
        q.addLast(t);

        // If currently locked, do not alert.
        if (locked.contains(key)) return null;

        // Alert exactly on the 3rd violation in the window.
        if (q.size() == 3) {
            locked.add(key); // lock until window fully clears
            String severity = r.violationSeverity(); // "RED LOW" or "RED HIGH"
            return Alert.from(r, severity);          // timestamp = r.timestamp
        }

        return null;
    }

    private void evictOld(Deque<Instant> q, Instant now) {
        while (!q.isEmpty()) {
            Instant head = q.peekFirst();
            // Inclusive 5 minute window: keep events where head >= now - 5min
            if (head.isBefore(now.minus(WINDOW))) {
                q.removeFirst();
            } else {
                break;
            }
        }
    }
}