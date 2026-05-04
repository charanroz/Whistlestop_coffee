package com.whistlestop_coffee.whistlestop_coffee.controller;

import com.whistlestop_coffee.whistlestop_coffee.model.StationSetting;
import com.whistlestop_coffee.whistlestop_coffee.service.StationSettingManager;
import org.springframework.web.bind.annotation.*;
import com.whistlestop_coffee.whistlestop_coffee.dto.LoginResult;

@RestController
@RequestMapping("/station-setting")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class StationSettingController {

    private final StationSettingManager stationSettingManager;

    public StationSettingController(StationSettingManager stationSettingManager) {
        this.stationSettingManager = stationSettingManager;
    }

    @GetMapping
    public StationSetting getStationSetting() {
        return stationSettingManager.getStationSetting();
    }

    @PutMapping
    public StationSetting updateStationSetting(@RequestBody StationSetting updatedSetting) {
        return stationSettingManager.updateStationSetting(updatedSetting);
    }
}