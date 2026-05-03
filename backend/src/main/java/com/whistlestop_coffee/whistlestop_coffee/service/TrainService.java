package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.Train;
import java.util.List;

public interface TrainService {
    List<Train> getIncomingTrains(String stationName);
    Train getTrainStatus(String trainId);
}
