package com.whistlestop_coffee.whistlestop_coffee.model;

public class StationSetting {

    private String stationName;
    private String kioskName;

    public StationSetting() {
    }

    public StationSetting(String stationName, String kioskName) {
        this.stationName = stationName;
        this.kioskName = kioskName;
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