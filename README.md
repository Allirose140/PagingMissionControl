# Paging Mission Control
Java implementation of the Enlighten Programming Challenge 2025

## Summary
This program reads satellite telemetry data from an input text file, detects repeated alerts for a satellite, and suppresses redundant alerts within a 5 minute sliding window. It processes telemetry efficiently in one pass using a per key sliding window.

## Build
mvn clean package

This creates a runnable JAR under:
target/paging-mission-control-1.0.0.jar

## Run
java -jar target/paging-mission-control-1.0.0.jar data/sample.txt

Replace "data/sample.txt" with your own input file path.

### Input Format
Each line of the input file represents one telemetry record, for example:
2025-10-12T08:15:30Z SAT-001 100 50 40 25 20

- First column: ISO timestamp
- Second: satellite identifier
- Remaining values: telemetry readings

### Output
JSON alerts are printed to standard output, e.g:
{"sat":"SAT-001","alert":"threshold violation","timestamp":"2025-10-12T08:15:30Z"}

## Design
- Single-pass O(n) processing
- Per-key Deque<Instant> sliding window
- Alert triggered on third violation within window
- 5-minute inclusive window for suppression

## Assumptions
- Input is well-formed (one telemetry record per line)
- Output is printed to console (not written to file)
- After one alert for a key, duplicates within the current window are suppressed

## Tests
mvn test

## Author
Allison Harvel  
Challenge: Enlighten Programming Challenge 2025