package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.BusinessHour;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BusinessHourManager {

    private final List<BusinessHour> businessHours = new ArrayList<>();

    public BusinessHourManager() {
        businessHours.add(new BusinessHour("Monday", "06:30", "19:00", false));
        businessHours.add(new BusinessHour("Tuesday", "06:30", "19:00", false));
        businessHours.add(new BusinessHour("Wednesday", "06:30", "19:00", false));
        businessHours.add(new BusinessHour("Thursday", "06:30", "19:00", false));
        businessHours.add(new BusinessHour("Friday", "06:30", "19:00", false));
        businessHours.add(new BusinessHour("Saturday", "07:00", "18:00", false));
        businessHours.add(new BusinessHour("Sunday", null, null, true));
    }

    public List<BusinessHour> getAllBusinessHours() {
        return businessHours;
    }

    public BusinessHour getBusinessHourByDay(String dayOfWeek) {
        for (BusinessHour businessHour : businessHours) {
            if (businessHour.getDayOfWeek().equalsIgnoreCase(dayOfWeek)) {
                return businessHour;
            }
        }

        throw new RuntimeException("Business hours not found for " + dayOfWeek);
    }

    public BusinessHour updateBusinessHour(String dayOfWeek, BusinessHour updatedBusinessHour) {
        BusinessHour businessHour = getBusinessHourByDay(dayOfWeek);

        businessHour.setOpenTime(updatedBusinessHour.getOpenTime());
        businessHour.setCloseTime(updatedBusinessHour.getCloseTime());
        businessHour.setClosed(updatedBusinessHour.isClosed());

        return businessHour;
    }

    public boolean isWithinBusinessHours(String dayOfWeek, String time) {
        BusinessHour businessHour = getBusinessHourByDay(dayOfWeek);

        if (businessHour.isClosed()) {
            return false;
        }

        if (businessHour.getOpenTime() == null || businessHour.getCloseTime() == null) {
            return false;
        }

        return time.compareTo(businessHour.getOpenTime()) >= 0
                && time.compareTo(businessHour.getCloseTime()) <= 0;
    }
}