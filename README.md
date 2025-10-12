Paging Mission Control:


Java implementation of the Enlighten Programming Challenge 2025

Summary: This program reads satellite telemetry data from an input file, detects repeated
alerts for a satellite, and suppresses redundant alerts within a 5 minute sliding window.


Build:

mvn clean package


Run:

java -jar target/paging-mission-control-1.0.0.jar <path-to-input.txt>


Design:

- One pass O(n) processing
- Per key Deque<Instant> sliding window
- Alert on 3rd violation
- 5 minute inclusive window for suppression


Assumptions:

- Input is well-formed (one telemetry record per line)
- Outputs one JSON alert per line to stdout
- Suppression policy A: after one alert for a key, suppress until the window clears


Tests:

mvn test


Author: Allison Harvel

Challenge: Enlighten Programming Challenge 2025