package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.Train;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Profile("mock")
@Service
public class MockTrainService implements TrainService {

    private final List<Train> activeTrains = new ArrayList<>();
    private final Random random = new Random();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @PostConstruct
    public void init() {
        generateMockTrains();
    }

    private void generateMockTrains() {
        LocalTime now = LocalTime.now();
        
        String[] origins = {"London Kings Cross", "Edinburgh Waverley", "York", "Leeds", "Manchester Piccadilly"};
        
        for (int i = 0; i < 15; i++) {
            LocalTime scheduled = now.plusMinutes(10 + (i * 15));
            String status = "On time";
            LocalTime estimated = scheduled;
            
            // Randomly delay some trains
            if (random.nextDouble() > 0.6) {
                int delayMins = 5 + random.nextInt(25);
                estimated = scheduled.plusMinutes(delayMins);
                status = "Delayed";
            }
            
            Train t = new Train(
                "TRN" + (1000 + random.nextInt(9000)),
                origins[random.nextInt(origins.length)],
                "Cramlington", // Assuming the station is Cramlington
                scheduled.format(timeFormatter),
                estimated.format(timeFormatter),
                status
            );
            activeTrains.add(t);
        }
    }

    @Override
    public List<Train> getIncomingTrains(String stationName) {
        // Occasionally update delays to simulate live data
        updateLiveDelays();
        return activeTrains;
    }

    @Override
    public Train getTrainStatus(String trainId) {
        return activeTrains.stream()
                .filter(t -> t.getTrainId().equals(trainId))
                .findFirst()
                .orElse(null);
    }
    
    private void updateLiveDelays() {
        if (random.nextDouble() > 0.7) { // 30% chance to update a train delay
            Train t = activeTrains.get(random.nextInt(activeTrains.size()));
            if (!t.getStatus().equals("Cancelled")) {
                LocalTime currentEst = LocalTime.parse(t.getEstimatedArrivalTime(), timeFormatter);
                LocalTime newEst = currentEst.plusMinutes(5); // delay by another 5 mins
                t.setEstimatedArrivalTime(newEst.format(timeFormatter));
                t.setStatus("Delayed");
            }
        }
    }
}
