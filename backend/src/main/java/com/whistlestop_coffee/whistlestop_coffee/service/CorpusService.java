package com.whistlestop_coffee.whistlestop_coffee.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads the NWR CORPUS reference data from the local JSON file
 * (backend/src/main/resources/CORPUSExtract.json) at startup.
 *
 * Provides TIPLOC → readable station name lookup.
 * Example: "KNGX" → "LONDON KINGS CROSS"
 *          "EDINBUR" → "EDINBURGH"
 *          "CRMLNGT" → "CRAMLINGTON"
 *
 * This avoids showing raw TIPLOC codes on the frontend train cards.
 */
@Service
public class CorpusService {

    // TIPLOC code → human-readable station name (from NLCDESC field)
    private final Map<String, String> tiplocToName = new HashMap<>();

    @PostConstruct
    public void loadCorpus() {
        try {
            InputStream stream = new ClassPathResource("CORPUSExtract.json").getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(stream);
            JsonNode data = root.get("TIPLOCDATA");

            if (data == null || !data.isArray()) {
                System.err.println("⚠️  CORPUS: TIPLOCDATA array not found");
                return;
            }

            int loaded = 0;
            for (JsonNode entry : data) {
                String tiploc = text(entry, "TIPLOC");
                String name   = text(entry, "NLCDESC");

                // Skip entries with no TIPLOC or empty name
                if (tiploc.isBlank() || name.isBlank()) continue;

                // Prefer NLCDESC16 (shorter, ~16 chars) if it's not blank
                String short16 = text(entry, "NLCDESC16");
                tiplocToName.put(tiploc, short16.isBlank() ? toTitleCase(name) : toTitleCase(short16));
                loaded++;
            }

            System.out.println("✅ CORPUS loaded: " + loaded + " station entries");

        } catch (Exception e) {
            System.err.println("❌ Failed to load CORPUS: " + e.getMessage());
        }
    }

    /**
     * Look up a human-readable station name from a TIPLOC code.
     * Returns the original TIPLOC if not found (graceful fallback).
     */
    public String getStationName(String tiploc) {
        if (tiploc == null || tiploc.isBlank()) return "Unknown";
        String name = tiplocToName.get(tiploc.trim().toUpperCase());
        return name != null ? name : tiploc; // fallback: show the raw code
    }

    /** Get size of the loaded map (for diagnostics) */
    public int size() {
        return tiplocToName.size();
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private String text(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asText("").trim() : "";
    }

    /**
     * Convert ALL CAPS station names to Title Case.
     * "LONDON KINGS CROSS" → "London Kings Cross"
     */
    private String toTitleCase(String input) {
        if (input == null || input.isBlank()) return input;
        String[] words = input.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) sb.append(word.substring(1));
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }
}
