package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.StationSetting;
import com.whistlestop_coffee.whistlestop_coffee.repository.StationSettingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationSettingManager {

    private final StationSettingRepository stationSettingRepository;

    public StationSettingManager(StationSettingRepository stationSettingRepository) {
        this.stationSettingRepository = stationSettingRepository;
    }

    public StationSetting getStationSetting() {
        List<StationSetting> settings = stationSettingRepository.findAll();

        if (settings.isEmpty()) {
            StationSetting defaultSetting = new StationSetting(
                    "Cramlington Station",
                    "Whistlestop Coffee Hut"
            );
            return stationSettingRepository.save(defaultSetting);
        }

        return settings.get(0);
    }

    public StationSetting updateStationSetting(StationSetting updatedSetting) {
        StationSetting setting = getStationSetting();

        setting.setStationName(updatedSetting.getStationName());
        setting.setKioskName(updatedSetting.getKioskName());

        return stationSettingRepository.save(setting);
    }
}