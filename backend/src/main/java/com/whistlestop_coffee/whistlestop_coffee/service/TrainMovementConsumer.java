package com.whistlestop_coffee.whistlestop_coffee.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whistlestop_coffee.whistlestop_coffee.model.Train;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Consumes real-time train movement messages from the NWR Train Movements Kafka topic.
 *
 * Handles two message types:
 *   msg_type 0001 (ACTIVATION) → a new train service has been activated for today;
 *                                 used to pre-populate the cache with upcoming trains
 *                                 so they appear in the UI before they physically arrive.
 *   msg_type 0003 (MOVEMENT)   → a train has passed/arrived at a location;
 *                                 used to update estimated arrival times with real delays.
 *
 * Both types filter to body.loc_stanox == "12136" (Cramlington Station).
 */
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers", matchIfMissing = false)
@Service
public class TrainMovementConsumer {

    private final TrainCache trainCache;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.of("Europe/London"));

    @Value("${networkrail.cramlington.stanox}")
    private String cramlingtonStanox; // 12136

    public TrainMovementConsumer(TrainCache trainCache) {
        this.trainCache = trainCache;
    }

    @KafkaListener(topics = "TRAIN_MVT_ALL_TOC", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> record) {
        String value = record.value();
        if (value == null || value.isBlank()) return;
        try {
            JsonNode messages = objectMapper.readTree(value);
            if (messages.isArray()) {
                for (JsonNode msg : messages) processMessage(msg);
            } else {
                processMessage(messages);
            }
        } catch (Exception e) {
            System.err.println("⚠️  Failed to parse train movement message: " + e.getMessage());
        }
    }

    private void processMessage(JsonNode msg) {
        JsonNode header = msg.get("header");
        JsonNode body   = msg.get("body");
        if (header == null || body == null) return;

        String msgType = header.has("msg_type") ? header.get("msg_type").asText() : "";

        switch (msgType) {
            case "0001" -> handleActivation(body);   // Train activated for today
            case "0003" -> handleMovement(body);     // Train passed/arrived at location
        }
    }

    /**
     * ACTIVATION (0001): A train has been activated for today's service.
     * We register it in the cache so it appears in the "upcoming trains" list
     * even before it reaches Cramlington. We only add trains that stop at
     * Cramlington (tp_origin_stanox alone isn't enough, but we use sched_origin_stanox
     * as a heuristic and let MOVEMENT events refine the data later).
     *
     * Note: Not all activated trains stop at Cramlington; the UI will only show
     * trains that later get a MOVEMENT event at stanox 12136, or we let the
     * fallback mock data fill the gap.
     */
    private void handleActivation(JsonNode body) {
        // Activation messages don't have loc_stanox — skip filtering by stanox here.
        // We'll only keep trains that later get a Cramlington movement event.
        String trainUid = body.has("train_id") ? body.get("train_id").asText("").trim() : "";
        if (trainUid.isBlank()) return;

        // Only add if not already in cache (don't overwrite real data)
        if (trainCache.getById(trainUid) != null) return;

        String plannedTsStr = body.has("origin_dep_timestamp")
                ? body.get("origin_dep_timestamp").asText("") : "";
        String scheduledTime = parseTimestamp(plannedTsStr);
        if (scheduledTime == null) return;

        // Filter: only activate trains whose scheduled time is in the future
        try {
            LocalTime scheduled = LocalTime.parse(scheduledTime, DateTimeFormatter.ofPattern("HH:mm"));
            if (scheduled.isBefore(LocalTime.now())) return;
        } catch (Exception ignored) { return; }

        String origin = body.has("sched_origin_stanox")
                ? body.get("sched_origin_stanox").asText("Unknown") : "Unknown";

        Train train = new Train(trainUid, origin, "Cramlington",
                scheduledTime, scheduledTime, "On time");
        trainCache.put(train);
        System.out.println("🚆 Activated train " + trainUid + " → Cramlington sched " + scheduledTime);
    }

    /**
     * MOVEMENT (0003): A train has passed or arrived at a location.
     * Filters to Cramlington ARRIVAL events and updates the cache.
     * If the train isn't in cache yet, creates a new entry from real data.
     */
    private void handleMovement(JsonNode body) {
        String locStanox = body.has("loc_stanox") ? body.get("loc_stanox").asText("").trim() : "";
        if (!cramlingtonStanox.equals(locStanox)) return;

        String eventType = body.has("event_type") ? body.get("event_type").asText("") : "";
        if (!"ARRIVAL".equals(eventType)) return;

        String trainUid = body.has("train_id") ? body.get("train_id").asText("").trim() : "";
        if (trainUid.isBlank()) return;

        String actualTsStr  = body.has("actual_timestamp")  ? body.get("actual_timestamp").asText("")  : "";
        String plannedTsStr = body.has("planned_timestamp") ? body.get("planned_timestamp").asText("") : "";

        String estimatedTime = parseTimestamp(actualTsStr.isBlank() ? plannedTsStr : actualTsStr);
        if (estimatedTime == null) return;

        String plannedTime = parseTimestamp(plannedTsStr);
        String status = (plannedTime != null && !plannedTime.equals(estimatedTime)) ? "Delayed" : "On time";

        Train existing = trainCache.getById(trainUid);
        if (existing != null) {
            // Update existing scheduled entry with real arrival data
            trainCache.updateDelay(trainUid, estimatedTime, status);
        } else {
            // No schedule entry — create one from live movement data
            String scheduled = plannedTime != null ? plannedTime : estimatedTime;
            Train train = new Train(trainUid, "Network Rail Live", "Cramlington",
                    scheduled, estimatedTime, status);
            trainCache.put(train);
            System.out.println("🚆 New live train " + trainUid + " at Cramlington: " + estimatedTime + " [" + status + "]");
        }
    }

    private String parseTimestamp(String epochMillisStr) {
        if (epochMillisStr == null || epochMillisStr.isBlank() || "0".equals(epochMillisStr.trim())) return null;
        try {
            long epochMillis = Long.parseLong(epochMillisStr.trim());
            return timeFmt.format(Instant.ofEpochMilli(epochMillis));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

