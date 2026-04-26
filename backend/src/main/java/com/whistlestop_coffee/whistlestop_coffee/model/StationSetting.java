package com.whistlestop_coffee.whistlestop_coffee.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class StationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String stationName;
    private String kioskName;

    public StationSetting() {}

    public StationSetting(String stationName, String kioskName) {
        this.stationName = stationName;
        this.kioskName = kioskName;
    }

    public int getId() { return id; }
    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
    public String getKioskName() { return kioskName; }
    public void setKioskName(String kioskName) { this.kioskName = kioskName; }
}