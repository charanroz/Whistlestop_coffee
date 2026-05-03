package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.Train;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory cache for real-time train data.
 * Populated by ScheduleFetchService (timetable) and updated by TrainMovementConsumer (live delays).
 * Thread-safe via ConcurrentHashMap.
 */
@Component
public class TrainCache {

    // Key: trainUid (e.g. "C12345") — matches what NWR Train Movements sends
    private final ConcurrentHashMap<String, Train> cache = new ConcurrentHashMap<>();

    /**
     * Store or replace a train entry.
     */
    public void put(Train train) {
        if (train != null && train.getTrainId() != null) {
            cache.put(train.getTrainId(), train);
        }
    }

    /**
     * Update a train's estimated arrival time and status from a live movement message.
     * Only updates if the train already exists in the cache (i.e. was in today's schedule).
     */
    public void updateDelay(String trainUid, String newEstimatedArrival, String status) {
        Train train = cache.get(trainUid);
        if (train != null) {
            train.setEstimatedArrivalTime(newEstimatedArrival);
            train.setStatus(status);
            System.out.println("🔄 Train " + trainUid + " updated: " + status + " → " + newEstimatedArrival);
        }
    }

    /**
     * Get all trains sorted by scheduled arrival time (earliest first).
     */
    public List<Train> getAllSorted() {
        List<Train> trains = new ArrayList<>(cache.values());
        trains.sort(Comparator.comparing(Train::getScheduledArrivalTime));
        return trains;
    }

    /**
     * Get a single train by its UID/ID.
     */
    public Train getById(String trainId) {
        return cache.get(trainId);
    }

    /**
     * Replace all entries (used when reloading today's schedule).
     */
    public void replaceAll(List<Train> trains) {
        cache.clear();
        trains.forEach(this::put);
        System.out.println("🚆 TrainCache refreshed with " + trains.size() + " trains to Cramlington");
    }

    public int size() {
        return cache.size();
    }
}
