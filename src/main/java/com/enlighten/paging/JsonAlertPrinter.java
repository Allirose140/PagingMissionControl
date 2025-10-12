package com.enlighten.paging;

public class JsonAlertPrinter implements AlertPrinter {
    @Override
    public void print(Alert a) {
        // Minimal, valid JSON; timestamp uses ISO-8601 via Instant.toString()
        String json =
                "{\"satelliteId\":" + a.satelliteId +
                        ",\"severity\":\"" + a.severity + "\"" +
                        ",\"component\":\"" + a.component + "\"" +
                        ",\"timestamp\":\"" + a.timestamp.toString() + "\"}";
        System.out.println(json);
    }
}