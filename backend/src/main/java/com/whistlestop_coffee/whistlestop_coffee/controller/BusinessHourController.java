package com.whistlestop_coffee.whistlestop_coffee.controller;

import com.whistlestop_coffee.whistlestop_coffee.model.BusinessHour;
import com.whistlestop_coffee.whistlestop_coffee.service.BusinessHourManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/business-hours")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BusinessHourController {

    private final BusinessHourManager businessHourManager;

    public BusinessHourController(BusinessHourManager businessHourManager) {
        this.businessHourManager = businessHourManager;
    }

    @GetMapping
    public List<BusinessHour> getAllBusinessHours() {
        return businessHourManager.getAllBusinessHours();
    }

    @GetMapping("/{dayOfWeek}")
    public BusinessHour getBusinessHourByDay(@PathVariable String dayOfWeek) {
        return businessHourManager.getBusinessHourByDay(dayOfWeek);
    }

    @PutMapping("/{dayOfWeek}")
    public BusinessHour updateBusinessHour(
            @PathVariable String dayOfWeek,
            @RequestBody BusinessHour updatedBusinessHour
    ) {
        return businessHourManager.updateBusinessHour(dayOfWeek, updatedBusinessHour);
    }

    @GetMapping("/check")
    public boolean checkBusinessHours(
            @RequestParam String dayOfWeek,
            @RequestParam String time
    ) {
        return businessHourManager.isWithinBusinessHours(dayOfWeek, time);
    }
}