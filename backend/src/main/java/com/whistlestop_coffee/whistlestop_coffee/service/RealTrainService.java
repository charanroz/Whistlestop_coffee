package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.Train;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Real implementation of TrainService backed by live NWR data.
 * Falls back to mock train data when the TrainCache is empty
 * (i.e. NR Schedule credentials not yet configured).
 *
 * @Primary ensures this is preferred over MockTrainService.
 */
@Primary
@Service
public class RealTrainService implements TrainService {

    private final TrainCache trainCache;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
    private final Random rng = new Random();

    // Realistic East Coast Main Line origins (trains that actually pass through Cramlington)
    private static final String[] MOCK_ORIGINS = {
        "London Kings Cross", "Edinburgh Waverley", "York", "Leeds",
        "Newcastle", "Durham", "Darlington", "Morpeth", "Berwick-Upon-Tweed"
    };

    public RealTrainService(TrainCache trainCache) {
        this.trainCache = trainCache;
    }

    @Override
    public List<Train> getIncomingTrains(String stationName) {
        List<Train> trains = trainCache.getAllSorted();

        // If cache is populated with real data, return it
        if (!trains.isEmpty()) return trains;

        // Fallback: generate mock trains so the UI is never empty
        System.out.println("⚠️  TrainCache empty — serving mock train data (configure NR credentials for live data)");
        return generateFallbackTrains();
    }

    @Override
    public Train getTrainStatus(String trainId) {
        Train cached = trainCache.getById(trainId);
        if (cached != null) return cached;

        // If not found in cache, check if it's a mock ID
        if (trainId != null && trainId.startsWith("MOCK-")) {
            // Return a basic on-time train for scheduler compatibility
            return new Train(trainId, "Unknown", "Cramlington",
                    LocalTime.now().plusMinutes(10).format(fmt),
                    LocalTime.now().plusMinutes(10).format(fmt),
                    "On time");
        }
        return null;
    }

    /**
     * Generate realistic-looking mock trains when NR Schedule isn't configured.
     * Produces 8 trains starting 10 minutes from now, spaced ~20 minutes apart.
     */
    private List<Train> generateFallbackTrains() {
        List<Train> result = new ArrayList<>();
        LocalTime now = LocalTime.now();

        for (int i = 0; i < 8; i++) {
            LocalTime scheduled = now.plusMinutes(10 + (i * 20L));
            LocalTime estimated = scheduled;
            String status = "On time";

            if (rng.nextDouble() > 0.65) {
                int delayMins = 5 + rng.nextInt(20);
                estimated = scheduled.plusMinutes(delayMins);
                status = "Delayed";
            }

            result.add(new Train(
                "MOCK-" + (1000 + i),
                MOCK_ORIGINS[rng.nextInt(MOCK_ORIGINS.length)],
                "Cramlington",
                scheduled.format(fmt),
                estimated.format(fmt),
                status
            ));
        }
        return result;
    }
}
