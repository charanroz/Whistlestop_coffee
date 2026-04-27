package com.whistlestop_coffee.whistlestop_coffee.model;

import jakarta.persistence.*;

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