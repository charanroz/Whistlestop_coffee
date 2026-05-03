package com.whistlestop_coffee.whistlestop_coffee.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.whistlestop_coffee.whistlestop_coffee.model.Train;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Consumes Darwin Push Port JSON messages from the NWR Schedule Kafka topic.
 *
 * Darwin is National Rail's official real-time train information system.
 * It provides:
 *   • schedule messages  → planned timetable (which trains call at Cramlington today)
 *   • TS (Train Status)  → real-time delay/cancellation updates per train/location
 *
 * This consumer populates TrainCache with upcoming Cramlington arrivals and
 * keeps their estimated times updated in real-time, replacing the need for the
 * OAuth2 CIF schedule download that RDM doesn't support via API.
 *
 * Topic: prod-1010-Darwin-Train-Information-Push-Port-IIII2_0-JSON
 */
@ConditionalOnProperty(name = "nwr.schedule.kafka.topic")
@Service
public class DarwinScheduleConsumer {

    private final TrainCache trainCache;
    private final CorpusService corpusService;
    private final ObjectMapper mapper = new ObjectMapper();

    // Log first N raw messages so we can verify the Darwin JSON structure
    private final AtomicInteger rawLogCount = new AtomicInteger(0);
    private static final int RAW_LOG_LIMIT = 2;

    @Value("${networkrail.cramlington.tiploc:CRMLNGT}")
    private String cramlingtonTiploc;

    public DarwinScheduleConsumer(TrainCache trainCache, CorpusService corpusService) {
        this.trainCache = trainCache;
        this.corpusService = corpusService;
    }

    /**
     * Listen to the Darwin Push Port topic with the NWR Schedule consumer group.
     * The groupId override ensures this listener uses a different offset tracking
     * from the NWR Train Movements consumer.
     */
    @KafkaListener(
        topics    = "${nwr.schedule.kafka.topic}",
        groupId   = "${nwr.schedule.kafka.group-id}"
    )
    public void consume(ConsumerRecord<String, String> record) {
        String value = record.value();
        if (value == null || value.isBlank()) return;

        // Log first few raw messages to verify the Darwin JSON structure
        int count = rawLogCount.incrementAndGet();
        if (count <= RAW_LOG_LIMIT) {
            System.out.println("🔍 Darwin raw msg #" + count + " (first 600 chars): "
                    + value.substring(0, Math.min(600, value.length())));
        }

        try {
            JsonNode root = mapper.readTree(value);
            processRoot(root);
        } catch (Exception e) {
            if (count <= 20) {
                System.err.println("⚠️  Darwin parse error: " + e.getMessage());
            }
        }
    }

    // ── Message dispatch ─────────────────────────────────────────────────────

    private void processRoot(JsonNode root) {
        // Darwin messages are wrapped: Pport → uR → TS / schedule
        JsonNode pport   = field(root, "Pport", "pport");
        JsonNode uR      = pport != null ? field(pport, "uR", "ur", "UR") : field(root, "uR", "ur", "UR");
        JsonNode context = uR != null ? uR : (pport != null ? pport : root);

        // Handle schedule messages (planned timetable)
        processEach(field(context, "schedule", "Schedule"), this::handleSchedule);

        // Handle TS messages (real-time status updates)
        processEach(field(context, "TS", "ts", "trainStatus", "TrainStatus"), this::handleTrainStatus);
    }

    private void processEach(JsonNode node, java.util.function.Consumer<JsonNode> handler) {
        if (node == null) return;
        if (node.isArray()) {
            for (JsonNode item : node) handler.accept(item);
        } else {
            handler.accept(node);
        }
    }

    // ── Schedule handler ─────────────────────────────────────────────────────

    /**
     * A schedule message tells us the planned route for one service today.
     * We check each calling point for Cramlington (TIPLOC: CRMLNGT) and, if
     * found, register the train in the cache with its planned arrival time.
     */
    private void handleSchedule(JsonNode s) {
        String rid = attr(s, "rid", "RID");
        String uid = attr(s, "uid", "UID");
        if (rid == null && uid == null) return;
        String trainId = rid != null ? rid : uid;

        if (trainCache.getById(trainId) != null) return; // already known

        String cramArrival = null;
        String originName  = null;

        // Darwin schedule location types: OR=origin, IP=intermediate, DT=destination, PP=passing
        for (String locType : new String[]{"OR", "or", "IP", "ip", "PP", "pp", "DT", "dt", "OPIP", "opip"}) {
            JsonNode locs = s.get(locType);
            if (locs == null) continue;
            if (!locs.isArray()) locs = wrapArray(locs);

            for (JsonNode loc : locs) {
                String tpl = attr(loc, "tpl", "TPL", "tiploc");

                // Capture origin name from first location we see
                if (originName == null && tpl != null && !tpl.isBlank()) {
                    originName = corpusService.getStationName(tpl);
                }

                if (cramlingtonTiploc.equalsIgnoreCase(tpl)) {
                    cramArrival = coalesce(attr(loc, "pta", "PTA"), attr(loc, "wta", "WTA"));
                }
            }
        }

        if (cramArrival == null) return;        // doesn't call at Cramlington
        cramArrival = hhmm(cramArrival);
        if (!isFuture(cramArrival)) return;     // train already passed

        String origin = originName != null ? originName : "Unknown";
        trainCache.put(new Train(trainId, origin, "Cramlington", cramArrival, cramArrival, "On time"));
        System.out.println("📅 Scheduled " + trainId + " [" + origin + " → Cramlington " + cramArrival + "]");
    }

    // ── Train Status handler ─────────────────────────────────────────────────

    /**
     * A TS message carries real-time estimated times for each location on a
     * service. We look for a Cramlington location entry and update the cache.
     */
    private void handleTrainStatus(JsonNode ts) {
        String rid = attr(ts, "rid", "RID");
        if (rid == null) return;

        JsonNode locations = coalesceNode(ts.get("Location"), ts.get("location"));
        if (locations == null) return;
        if (!locations.isArray()) locations = wrapArray(locations);

        for (JsonNode loc : locations) {
            String tpl = attr(loc, "tpl", "TPL", "tiploc");
            if (!cramlingtonTiploc.equalsIgnoreCase(tpl)) continue;

            // Prefer actual time (at) over estimated (et)
            JsonNode arrNode = coalesceNode(loc.get("arr"), loc.get("Arr"));
            if (arrNode == null) continue;

            String et        = attr(arrNode, "et", "ET");
            String at        = attr(arrNode, "at", "AT");
            String estimated = hhmm(coalesce(at, et));
            if (estimated == null) continue;

            // Check cancellation
            boolean cancelled = "true".equalsIgnoreCase(attr(ts, "is_cancelled", "isCancelled"))
                             || "true".equalsIgnoreCase(attr(arrNode, "isCancelled", "is_cancelled"));
            String status;
            if (cancelled) {
                status = "Cancelled";
            } else {
                String planned = hhmm(coalesce(attr(loc, "pta", "PTA"), attr(loc, "wta", "WTA")));
                status = (planned != null && !planned.equals(estimated)) ? "Delayed" : "On time";
            }

            Train existing = trainCache.getById(rid);
            if (existing != null) {
                trainCache.updateDelay(rid, estimated, status);
            } else if (!"Cancelled".equals(status) && isFuture(estimated)) {
                // Build a new entry purely from live TS data
                String planned = hhmm(attr(loc, "pta", "PTA"));
                if (planned == null) planned = estimated;
                trainCache.put(new Train(rid, "Darwin Live", "Cramlington", planned, estimated, status));
                System.out.println("🚆 Live train " + rid + " → Cramlington " + estimated + " [" + status + "]");
            }
        }
    }

    // ── Utilities ────────────────────────────────────────────────────────────

    /** Read a field, trying plain name and XML @-prefixed name. */
    private String attr(JsonNode node, String... names) {
        if (node == null) return null;
        for (String name : names) {
            for (String candidate : new String[]{name, "@" + name}) {
                JsonNode f = node.get(candidate);
                if (f != null && !f.isNull() && !f.asText("").isBlank()) return f.asText().trim();
            }
        }
        return null;
    }

    /** Get first non-null child field from a node. */
    private JsonNode field(JsonNode node, String... names) {
        if (node == null) return null;
        for (String name : names) {
            JsonNode f = node.get(name);
            if (f != null && !f.isNull()) return f;
        }
        return null;
    }

    private JsonNode coalesceNode(JsonNode... nodes) {
        for (JsonNode n : nodes) if (n != null && !n.isNull()) return n;
        return null;
    }

    private String coalesce(String... vals) {
        for (String v : vals) if (v != null && !v.isBlank()) return v;
        return null;
    }

    /** Truncate to HH:mm, handling "HH:mm:ss" and "HH:mm" formats. */
    private String hhmm(String time) {
        if (time == null || time.isBlank()) return null;
        return time.length() > 5 ? time.substring(0, 5) : time;
    }

    /** Wrap a single JsonNode into a 1-element ArrayNode for uniform iteration. */
    private ArrayNode wrapArray(JsonNode node) {
        ArrayNode arr = mapper.createArrayNode();
        arr.add(node);
        return arr;
    }

    /** Returns true if the given HH:mm time is between now and 4 hours from now. */
    private boolean isFuture(String time) {
        if (time == null) return false;
        try {
            LocalTime t   = LocalTime.parse(time);
            LocalTime now = LocalTime.now();
            return t.isAfter(now) && t.isBefore(now.plusHours(4));
        } catch (Exception e) {
            return false;
        }
    }
}
