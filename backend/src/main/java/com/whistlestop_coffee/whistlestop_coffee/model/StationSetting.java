package com.whistlestop_coffee.whistlestop_coffee.model;

import jakarta.persistence.*;

/**
 * Global configuration singleton stored in the database.
 * Allows staff to reconfigure the kiosk location dynamically via the dashboard
 * without needing to alter backend properties or redeploy the app.
 */
@Entity
public class StationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stationName;
    private String kioskName;

    public StationSetting() {
    }

    public StationSetting(String stationName, String kioskName) {
        this.stationName = stationName;
        this.kioskName = kioskName;
    }

    public Long getId() {
        return id;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getKioskName() {
        return kioskName;
    }

    public void setKioskName(String kioskName) {
        this.kioskName = kioskName;
    }
}