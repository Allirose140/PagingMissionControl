package com.enlighten.paging;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MissionControl {

    public static void main(String[] args) {
        // 1) Require an input file path
        if (args.length == 0) {
            System.out.println("Usage: java -jar paging-mission-control.jar <path-to-input-file>");
            return;
        }

        String inputPath = args[0];
        System.out.println("Reading telemetry from: " + inputPath);

        // 2) Helpers: window engine + JSON printer
        SlidingWindowEngine engine = new SlidingWindowEngine();
        AlertPrinter printer = new JsonAlertPrinter();

        // 3) Read the file line-by-line, parse, process, print alerts
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) continue; // skip blanks/comments

                TelemetryRecord record = TelemetryRecord.fromLine(line);
                if (record == null) continue; // defensive: skip unparseable lines if any

                Alert alert = engine.process(record);  // returns null or an alert
                if (alert != null) {
                    printer.print(alert);              // print one JSON object per alert
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}