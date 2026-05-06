package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.Train;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Primary
@Service
public class RealTrainService implements TrainService {

    private static final String CRAMLINGTON_CRS = "CRM";

    private final LiveDepartureBoardService liveDepartureBoardService;

    @Value("${rail.ldb.num-rows:20}")
    private int numRows;

    public RealTrainService(LiveDepartureBoardService liveDepartureBoardService) {
        this.liveDepartureBoardService = liveDepartureBoardService;
    }

    @Override
    public List<Train> getIncomingTrains(String stationName) {
        return liveDepartureBoardService.getDepartureBoard(CRAMLINGTON_CRS, numRows);
    }

    @Override
    public Train getTrainStatus(String trainId) {
        if (trainId == null || trainId.isBlank()) {
            return null;
        }

        return liveDepartureBoardService.getDepartureBoard(CRAMLINGTON_CRS, numRows).stream()
                .filter(train -> trainId.equals(train.getTrainId()))
                .findFirst()
                .orElse(null);
    }
}
