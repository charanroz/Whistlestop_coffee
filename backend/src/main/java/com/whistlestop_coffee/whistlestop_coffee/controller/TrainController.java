package com.whistlestop_coffee.whistlestop_coffee.controller;

import com.whistlestop_coffee.whistlestop_coffee.model.Train;
import com.whistlestop_coffee.whistlestop_coffee.service.TrainService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trains")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @GetMapping("/arrivals")
    public List<Train> getIncomingTrains(@RequestParam(defaultValue = "Cramlington") String stationName) {
        return trainService.getIncomingTrains(stationName);
    }
}
