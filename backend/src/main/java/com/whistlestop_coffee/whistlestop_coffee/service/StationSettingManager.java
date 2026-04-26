package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.StationSetting;
import com.whistlestop_coffee.whistlestop_coffee.repository.StationSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StationSettingManager {

    @Autowired
    private StationSettingRepository stationSettingRepository;

    public StationSetting getStationSetting() {
        return stationSettingRepository.findAll()
                .stream()
                .findFirst()
                .orElse(new StationSetting("Cramlington Station", "Whistlestop Coffee Hut"));
    }

    public StationSetting updateStationSetting(StationSetting updatedSetting) {
        StationSetting setting = getStationSetting();
        setting.setStationName(updatedSetting.getStationName());
        setting.setKioskName(updatedSetting.getKioskName());
        return stationSettingRepository.save(setting);
    }
}