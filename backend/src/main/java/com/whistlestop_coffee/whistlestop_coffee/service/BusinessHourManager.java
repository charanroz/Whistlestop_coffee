package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.BusinessHour;
import com.whistlestop_coffee.whistlestop_coffee.repository.BusinessHourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessHourManager {

    @Autowired
    private BusinessHourRepository businessHourRepository;

    public BusinessHourManager() {
        // Data will be loaded from database
    }

    public List<BusinessHour> getAllBusinessHours() {
        return businessHourRepository.findAll();
    }

    public BusinessHour getBusinessHourByDay(String dayOfWeek) {
        return businessHourRepository.findByDayOfWeekIgnoreCase(dayOfWeek)
                .orElseThrow(() -> new RuntimeException("Business hours not found for " + dayOfWeek));
    }

    public BusinessHour updateBusinessHour(String dayOfWeek, BusinessHour updatedBusinessHour) {
        BusinessHour businessHour = getBusinessHourByDay(dayOfWeek);
        businessHour.setOpenTime(updatedBusinessHour.getOpenTime());
        businessHour.setCloseTime(updatedBusinessHour.getCloseTime());
        businessHour.setClosed(updatedBusinessHour.isClosed());
        return businessHourRepository.save(businessHour);
    }

    public boolean isWithinBusinessHours(String dayOfWeek, String time) {
        BusinessHour businessHour = getBusinessHourByDay(dayOfWeek);
        if (businessHour.isClosed()) return false;
        if (businessHour.getOpenTime() == null || businessHour.getCloseTime() == null) return false;
        return time.compareTo(businessHour.getOpenTime()) >= 0
                && time.compareTo(businessHour.getCloseTime()) <= 0;
    }
}