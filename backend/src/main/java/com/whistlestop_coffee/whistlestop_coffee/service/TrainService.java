package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.Train;
import java.util.List;

/**
 * Abstraction for train data retrieval.
 * Allows seamless switching between Mock data (for local testing) and Live API (for production).
 */
public interface TrainService {
    List<Train> getIncomingTrains(String stationName);
    Train getTrainStatus(String trainId);
}
