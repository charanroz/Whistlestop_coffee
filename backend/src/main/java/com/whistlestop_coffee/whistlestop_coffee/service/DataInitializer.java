package com.whistlestop_coffee.whistlestop_coffee.service;

// This class initializes default data when the Spring Boot application starts.
// It adds the default kiosk business hours and station settings to the database
// if they do not already exist.

import com.whistlestop_coffee.whistlestop_coffee.model.BusinessHour;
import com.whistlestop_coffee.whistlestop_coffee.model.StationSetting;
import com.whistlestop_coffee.whistlestop_coffee.repository.BusinessHourRepository;
import com.whistlestop_coffee.whistlestop_coffee.repository.StationSettingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BusinessHourRepository businessHourRepository;
    private final StationSettingRepository stationSettingRepository;

    public DataInitializer(
            BusinessHourRepository businessHourRepository,
            StationSettingRepository stationSettingRepository
    ) {
        this.businessHourRepository = businessHourRepository;
        this.stationSettingRepository = stationSettingRepository;
    }

    @Override
    public void run(String... args) {
        if (businessHourRepository.count() == 0) {
            businessHourRepository.save(new BusinessHour("Monday", "06:30", "19:00", false));
            businessHourRepository.save(new BusinessHour("Tuesday", "06:30", "19:00", false));
            businessHourRepository.save(new BusinessHour("Wednesday", "06:30", "19:00", false));
            businessHourRepository.save(new BusinessHour("Thursday", "06:30", "19:00", false));
            businessHourRepository.save(new BusinessHour("Friday", "06:30", "19:00", false));
            businessHourRepository.save(new BusinessHour("Saturday", "07:00", "18:00", false));
            businessHourRepository.save(new BusinessHour("Sunday", null, null, true));
        }

        if (stationSettingRepository.count() == 0) {
            stationSettingRepository.save(
                    new StationSetting("Cramlington Station", "Whistlestop Coffee Hut")
            );
        }
    }
}