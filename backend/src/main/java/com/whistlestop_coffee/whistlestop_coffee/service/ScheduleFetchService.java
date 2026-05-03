package com.whistlestop_coffee.whistlestop_coffee.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whistlestop_coffee.whistlestop_coffee.model.Train;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Fetches today's NWR Schedule (CIF JSON) from Rail Data Marketplace.
 *
 * Flow:
 *   1. POST to RDM OAuth2 endpoint → get Bearer token (auto-refreshed)
 *   2. POST to RDM signed URL endpoint → get temporary S3 download link
 *   3. GET the S3 link → stream + parse CIF JSON for Cramlington arrivals
 *   4. Update TrainCache with today's timetable
 *
 * Runs on startup and refreshes every day at 06:00.
 */
@Service
public class ScheduleFetchService {

    private final TrainCache trainCache;
    private final CorpusService corpusService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ── RDM Auth (OAuth2 password grant) ────────────────────────────
    private static final String RDM_TOKEN_URL =
            "https://login.raildata.org.uk/oauth2/token";
    private static final String RDM_CLIENT_ID =
            "IIo28mF3HzPmLGm_vZIMhmAI964a";

    @Value("${rdm.username:}")
    private String rdmUsername;       // your RDM login email

    @Value("${rdm.password:}")
    private String rdmPassword;       // your RDM login password

    // ── RDM Signed URL endpoint ──────────────────────────────────────
    private static final String RDM_SIGNED_URL_ENDPOINT =
            "https://raildata.org.uk/ContractManagementService/cloudstore/generate/signedurl/download";

    @Value("${rdm.schedule.dsCode:DSP-650f2bb9-f9f7-4dd1-a47f-51271f10b253}")
    private String dsCode;

    @Value("${rdm.schedule.dataProductCode:}")
    private String dataProductCode;   // P-xxxxxxxx (fill in application.properties)

    private static final String DS_STATUS = "Active";
    private static final String FILE_NAME = "CIF_ALL_FULL_DAILY_toc-full.json.gz";

    // ── Cramlington filter ───────────────────────────────────────────
    @Value("${networkrail.cramlington.tiploc:CRMLNGT}")
    private String cramlingtonTiploc;

    // ── Token cache ──────────────────────────────────────────────────
    private String cachedToken = null;
    private long tokenExpiresAt = 0; // epoch millis

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public ScheduleFetchService(TrainCache trainCache, CorpusService corpusService) {
        this.trainCache = trainCache;
        this.corpusService = corpusService;
    }

    @PostConstruct
    public void init() {
        System.out.println("📅 Loading today's schedule for Cramlington (TIPLOC: CRMLNGT)...");
        loadSchedule();
    }

    /** Refresh every day at 06:00 (new timetable published ~01:30) */
    @Scheduled(cron = "0 0 6 * * *")
    public void dailyRefresh() {
        System.out.println("🔄 Daily schedule refresh...");
        loadSchedule();
    }

    private void loadSchedule() {
        if (!isConfigured()) {
            System.out.println("⚠️  RDM credentials not configured in application.properties — using mock fallback");
            return;
        }
        try {
            String bearer = getBearerToken();
            if (bearer == null) {
                System.err.println("❌ Failed to obtain RDM Bearer token");
                return;
            }
            String signedUrl = getSignedDownloadUrl(bearer);
            if (signedUrl == null) {
                System.err.println("❌ Failed to obtain signed download URL from RDM");
                return;
            }
            List<Train> trains = downloadAndParse(signedUrl);
            if (!trains.isEmpty()) {
                trainCache.replaceAll(trains);
            } else {
                System.out.println("⚠️  No Cramlington trains found in today's schedule");
            }
        } catch (Exception e) {
            System.err.println("❌ Schedule load failed: " + e.getMessage());
        }
    }

    // ── Step 1: OAuth2 token (password grant, cached) ────────────────

    private String getBearerToken() throws Exception {
        // Return cached token if still valid (with 5-min buffer)
        if (cachedToken != null && System.currentTimeMillis() < tokenExpiresAt - 300_000) {
            return cachedToken;
        }

        System.out.println("🔑 Refreshing RDM Bearer token...");

        String body = "grant_type=password"
                + "&username=" + URLEncoder.encode(rdmUsername, StandardCharsets.UTF_8)
                + "&password=" + URLEncoder.encode(rdmPassword, StandardCharsets.UTF_8)
                + "&client_id=" + URLEncoder.encode(RDM_CLIENT_ID, StandardCharsets.UTF_8)
                + "&scope=openid";

        HttpURLConnection conn = openPost(RDM_TOKEN_URL, null, "application/x-www-form-urlencoded");
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        int code = conn.getResponseCode();
        if (code != 200) {
            System.err.println("❌ Token request failed: HTTP " + code);
            return null;
        }

        JsonNode resp = objectMapper.readTree(conn.getInputStream());
        String token = resp.has("access_token") ? resp.get("access_token").asText() : null;
        long expiresIn = resp.has("expires_in") ? resp.get("expires_in").asLong(3600) : 3600;
        cachedToken = token;
        tokenExpiresAt = System.currentTimeMillis() + expiresIn * 1000;

        System.out.println("✅ RDM token obtained (expires in " + expiresIn + "s)");
        return token;
    }

    // ── Step 2: Generate signed S3 download URL ──────────────────────

    private String getSignedDownloadUrl(String bearer) throws Exception {
        String body = objectMapper.writeValueAsString(new java.util.HashMap<>() {{
            put("dsCode", dsCode);
            put("dataProductCode", dataProductCode);
            put("dsStatus", DS_STATUS);
            put("fileName", FILE_NAME);
        }});

        HttpURLConnection conn = openPost(RDM_SIGNED_URL_ENDPOINT, bearer, "application/json");
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        int code = conn.getResponseCode();
        if (code != 200) {
            System.err.println("❌ Signed URL request failed: HTTP " + code);
            return null;
        }

        JsonNode resp = objectMapper.readTree(conn.getInputStream());
        // RDM returns {"url": "https://s3.amazonaws.com/...?signature=..."}
        if (resp.has("url")) return resp.get("url").asText();
        if (resp.has("signedUrl")) return resp.get("signedUrl").asText();
        if (resp.has("downloadUrl")) return resp.get("downloadUrl").asText();

        System.err.println("⚠️  Unexpected signed URL response: " + resp);
        return null;
    }

    // ── Step 3: Download gzip JSON, parse for Cramlington ────────────

    private List<Train> downloadAndParse(String signedUrl) throws Exception {
        List<Train> result = new ArrayList<>();

        URL url = new URL(signedUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(30_000);
        conn.setReadTimeout(180_000); // 127 MB file, allow 3 minutes

        String encoding = conn.getContentEncoding();
        BufferedReader reader;
        if ("gzip".equalsIgnoreCase(encoding) || signedUrl.contains(".gz")) {
            reader = new BufferedReader(
                    new InputStreamReader(new GZIPInputStream(conn.getInputStream()), StandardCharsets.UTF_8));
        } else {
            reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        }

        LocalTime now = LocalTime.now();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isBlank()) continue;
            try {
                Train t = parseScheduleLine(line, now);
                if (t != null) result.add(t);
            } catch (Exception ignored) {}
        }
        reader.close();

        System.out.println("📋 Parsed " + result.size() + " Cramlington arrivals from RDM Schedule");
        return result;
    }

    private Train parseScheduleLine(String line, LocalTime now) throws Exception {
        JsonNode node = objectMapper.readTree(line);
        JsonNode schedule = node.get("JsonScheduleV1");
        if (schedule == null) return null;

        JsonNode segment = schedule.get("schedule_segment");
        if (segment == null) return null;

        JsonNode stops = segment.get("schedule_location");
        if (stops == null || !stops.isArray()) return null;

        String trainUid = schedule.has("CIF_train_uid")
                ? schedule.get("CIF_train_uid").asText("") : "";

        for (JsonNode stop : stops) {
            String tiploc = stop.has("tiploc_code")
                    ? stop.get("tiploc_code").asText("").trim() : "";

            if (!cramlingtonTiploc.equals(tiploc)) continue;

            String arrival = formatCifTime(stop.has("arrival") ? stop.get("arrival").asText() : "");
            String departure = formatCifTime(stop.has("departure") ? stop.get("departure").asText() : "");
            String arrivalTime = arrival.isBlank() ? departure : arrival;
            if (arrivalTime.isBlank()) return null;

            try {
                if (LocalTime.parse(arrivalTime, timeFormatter).isBefore(now)) return null;
            } catch (Exception e) { return null; }

            // Use TIPLOC of first stop as origin, resolved to readable name via CORPUS
            String originTiploc = stops.size() > 0 && stops.get(0).has("tiploc_code")
                    ? stops.get(0).get("tiploc_code").asText("").trim() : "";
            String origin = corpusService.getStationName(originTiploc);

            return new Train(
                    trainUid.isBlank() ? "T" + System.nanoTime() : trainUid,
                    origin, "Cramlington",
                    arrivalTime, arrivalTime, "On time"
            );
        }
        return null;
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private HttpURLConnection openPost(String urlStr, String bearer, String contentType) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15_000);
        conn.setReadTimeout(30_000);
        conn.setRequestProperty("Content-Type", contentType);
        if (bearer != null) {
            conn.setRequestProperty("Authorization", "Bearer " + bearer);
        }
        return conn;
    }

    /** Convert CIF time "HHMM" or "HHMM H" (half-minute) → "HH:mm" */
    private String formatCifTime(String cif) {
        if (cif == null || cif.isBlank()) return "";
        String clean = cif.trim().replaceAll("[^0-9]", "");
        if (clean.length() >= 4) {
            return clean.substring(0, 2) + ":" + clean.substring(2, 4);
        }
        return "";
    }

    private boolean isConfigured() {
        return rdmUsername != null && !rdmUsername.isBlank()
                && rdmPassword != null && !rdmPassword.isBlank()
                && dataProductCode != null && !dataProductCode.isBlank();
    }
}
