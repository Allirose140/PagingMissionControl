package com.enlighten.paging;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;

public class SlidingWindowEngineTest {

    private TelemetryRecord batt(Instant t, int sat, int redHigh, int yHi, int yLo, int redLow, int raw) {
        return new TelemetryRecord(t, sat, redHigh, yHi, yLo, redLow, raw, "BATT");
    }
    private TelemetryRecord tstat(Instant t, int sat, int redHigh, int yHi, int yLo, int redLow, int raw) {
        return new TelemetryRecord(t, sat, redHigh, yHi, yLo, redLow, raw, "TSTAT");
    }

    @Test
    void triggersOnThirdBattLowWithin5Min() {
        SlidingWindowEngine eng = new SlidingWindowEngine();
        Instant t0 = Instant.parse("2018-01-01T23:00:00Z");

        assertNull(eng.process(batt(t0,                    100, 50, 40, 25, 20, 18)));
        assertNull(eng.process(batt(t0.plusSeconds(60),    100, 50, 40, 25, 20, 19)));
        Alert a = eng.process(    batt(t0.plusSeconds(240),100, 50, 40, 25, 20, 17));
        assertNotNull(a);
        assertEquals("RED LOW", a.severity);
        assertEquals("BATT", a.component);
        assertEquals(100, a.satelliteId);
    }

    @Test
    void noAlertWhenSpreadBeyond5Min() {
        SlidingWindowEngine eng = new SlidingWindowEngine();
        Instant t0 = Instant.parse("2018-01-01T23:00:00Z");

        assertNull(eng.process(batt(t0,                    100, 50, 40, 25, 20, 18)));
        assertNull(eng.process(batt(t0.plusSeconds(120),   100, 50, 40, 25, 20, 19)));
        Alert a = eng.process(    batt(t0.plusSeconds(360),100, 50, 40, 25, 20, 17)); // 6 min later
        assertNull(a);
    }

    @Test
    void boundaryInclusiveAtFiveMinutes() {
        SlidingWindowEngine eng = new SlidingWindowEngine();
        Instant t0 = Instant.parse("2018-01-01T23:00:00Z");

        assertNull(eng.process(tstat(t0,                    100, 55, 50, 25, 20, 56)));
        assertNull(eng.process(tstat(t0.plusSeconds(299),   100, 55, 50, 25, 20, 60)));
        Alert a = eng.process(    tstat(t0.plusSeconds(300),100, 55, 50, 25, 20, 70)); // exactly +5:00
        assertNotNull(a);
        assertEquals("RED HIGH", a.severity);
        assertEquals("TSTAT", a.component);
    }
}