package com.whistlestop_coffee.whistlestop_coffee.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whistlestop_coffee.whistlestop_coffee.model.Train;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class LiveDepartureBoardService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${rail.ldb.api-key:}")
    private String apiKey;

    @Value("${rail.ldb.base-url:https://api1.raildata.org.uk/1010-live-departure-board-dep1_2/LDBWS/api/20220120}")
    private String baseUrl;

    @Value("${networkrail.cramlington.crs:CRM}")
    private String defaultCrs;

    public LiveDepartureBoardService() {
        this.restClient = RestClient.create();
        this.objectMapper = new ObjectMapper();
    }

    public List<Train> getDepartureBoard(String crs, int numRows) {
        if (apiKey == null || apiKey.isBlank()) {
            System.out.println("Live Departure Board API key missing. Set RAIL_LDB_API_KEY.");
            return List.of();
        }

        String stationCrs = (crs == null || crs.isBlank()) ? defaultCrs : crs.trim().toUpperCase();
        String url = baseUrl + "/GetDepartureBoard/" + stationCrs + "?numRows=" + numRows;

        try {
            String response = restClient.get()
                    .uri(url)
                    .header("x-apikey", apiKey)
                    .retrieve()
                    .body(String.class);

            return parseDepartureBoard(response);
        } catch (Exception ex) {
            System.err.println("Live Departure Board request failed: " + ex.getMessage());
            return List.of();
        }
    }

    private List<Train> parseDepartureBoard(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        JsonNode services = root.path("trainServices");
        if (!services.isArray()) {
            return List.of();
        }

        List<Train> trains = new ArrayList<>();
        for (int i = 0; i < services.size(); i++) {
            JsonNode service = services.get(i);
            String scheduled = text(service, "std");
            String estimated = estimatedFromEtd(text(service, "etd"), scheduled);
            String status = statusFromEtd(text(service, "etd"));
            String origin = firstLocationName(service.path("origin"));
            String destination = firstLocationName(service.path("destination"));
            String trainId = firstNonBlank(text(service, "serviceID"), text(service, "rsid"));

            if (trainId.isBlank()) {
                trainId = "LDB-" + scheduled + "-" + i;
            }

            trains.add(new Train(
                    trainId,
                    origin.isBlank() ? "Unknown" : origin,
                    destination.isBlank() ? "Unknown" : destination,
                    scheduled,
                    estimated,
                    status
            ));
        }
        return trains;
    }

    private String statusFromEtd(String etd) {
        if (etd == null || etd.isBlank() || etd.equalsIgnoreCase("On time")) {
            return "On time";
        }
        if (etd.equalsIgnoreCase("Cancelled")) {
            return "Cancelled";
        }
        return "Delayed";
    }

    private String estimatedFromEtd(String etd, String scheduled) {
        if (etd == null || etd.isBlank() || etd.equalsIgnoreCase("On time") || etd.equalsIgnoreCase("Cancelled")) {
            return scheduled;
        }
        return etd;
    }

    private String firstLocationName(JsonNode locations) {
        if (!locations.isArray() || locations.size() == 0) {
            return "";
        }
        return text(locations.get(0), "locationName");
    }

    private String text(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? "" : value.asText("");
    }

    private String firstNonBlank(String first, String second) {
        return first == null || first.isBlank() ? (second == null ? "" : second) : first;
    }
}
