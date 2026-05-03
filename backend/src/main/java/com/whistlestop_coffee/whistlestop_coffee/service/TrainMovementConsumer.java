package com.whistlestop_coffee.whistlestop_coffee.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Consumes real-time train movement messages from the NWR Train Movements Kafka topic.
 *
 * Topic: TRAIN_MVT_ALL_TOC
 * Broker: Confluent Cloud (SASL_SSL / PLAIN)
 *
 * Message format: JSON array of objects, each with a "header" and "body".
 * header.msg_type == "0003" → Movement event
 * body.loc_stanox == "12136" → Cramlington station
 * body.event_type == "ARRIVAL" → Arrival event
 *
 * When a delayed arrival is detected for a Cramlington train,
 * the TrainCache is updated with the new estimated time.
 */
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers", matchIfMissing = false)
@Service
public class TrainMovementConsumer {

    private final TrainCache trainCache;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.of("Europe/London"));

    @Value("${networkrail.cramlington.stanox}")
    private String cramlingtonStanox; // 12136

    public TrainMovementConsumer(TrainCache trainCache) {
        this.trainCache = trainCache;
    }

    /**
     * Listens to the NWR Train Movements Kafka topic.
     * Messages are batched — each record value is a JSON array of movement events.
     */
    @KafkaListener(topics = "TRAIN_MVT_ALL_TOC", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> record) {
        String value = record.value();
        if (value == null || value.isBlank()) return;

        try {
            JsonNode messages = objectMapper.readTree(value);

            // Messages can be a single object OR an array
            if (messages.isArray()) {
                for (JsonNode msg : messages) {
                    processMessage(msg);
                }
            } else {
                processMessage(messages);
            }

        } catch (Exception e) {
            // Don't let parsing errors kill the listener
            System.err.println("⚠️  Failed to parse train movement message: " + e.getMessage());
        }
    }

    private void processMessage(JsonNode msg) {
        JsonNode header = msg.get("header");
        JsonNode body = msg.get("body");

        if (header == null || body == null) return;

        // Only process msg_type 0003 (Movement events)
        String msgType = header.has("msg_type") ? header.get("msg_type").asText() : "";
        if (!"0003".equals(msgType)) return;

        // Only process events at Cramlington (STANOX 12136)
        String locStanox = body.has("loc_stanox") ? body.get("loc_stanox").asText("").trim() : "";
        if (!cramlingtonStanox.equals(locStanox)) return;

        // Only process ARRIVAL events
        String eventType = body.has("event_type") ? body.get("event_type").asText("") : "";
        if (!"ARRIVAL".equals(eventType)) return;

        // Extract train UID (used as our trainId key in the cache)
        String trainUid = body.has("train_id") ? body.get("train_id").asText("").trim() : "";
        if (trainUid.isBlank()) return;

        // Get actual timestamp (milliseconds epoch)
        String actualTsStr = body.has("actual_timestamp") ? body.get("actual_timestamp").asText("") : "";
        String plannedTsStr = body.has("planned_timestamp") ? body.get("planned_timestamp").asText("") : "";

        String estimatedTime = parseTimestamp(actualTsStr.isBlank() ? plannedTsStr : actualTsStr);
        if (estimatedTime == null) return;

        // Determine delay status
        String plannedTime = parseTimestamp(plannedTsStr);
        String status = "On time";
        if (plannedTime != null && !plannedTime.equals(estimatedTime)) {
            status = "Delayed";
        }

        // Update the cache — this flows through to TrainStatusScheduler → order pickup times
        trainCache.updateDelay(trainUid, estimatedTime, status);
    }

    /**
     * Convert epoch milliseconds string to "HH:mm" format in London time.
     */
    private String parseTimestamp(String epochMillisStr) {
        if (epochMillisStr == null || epochMillisStr.isBlank() || "0".equals(epochMillisStr.trim())) {
            return null;
        }
        try {
            long epochMillis = Long.parseLong(epochMillisStr.trim());
            return timeFormatter.format(Instant.ofEpochMilli(epochMillis));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
