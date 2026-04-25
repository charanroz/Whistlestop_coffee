package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.StationSetting;
import org.springframework.stereotype.Service;

@Service
public class StationSettingManager {

    private StationSetting stationSetting = new StationSetting(
            "Cramlington Station",
            "Whistlestop Coffee Hut"
    );

    public StationSetting getStationSetting() {
        return stationSetting;
    }

    public StationSetting updateStationSetting(StationSetting updatedSetting) {
        stationSetting.setStationName(updatedSetting.getStationName());
        stationSetting.setKioskName(updatedSetting.getKioskName());

        return stationSetting;
    }
}