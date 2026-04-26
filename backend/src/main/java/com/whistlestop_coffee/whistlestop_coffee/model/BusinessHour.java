package com.whistlestop_coffee.whistlestop_coffee.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class BusinessHour {

    @Id
    private String dayOfWeek;
    private String openTime;
    private String closeTime;
    private boolean closed;

    public BusinessHour() {
    }

    public BusinessHour(String dayOfWeek, String openTime, String closeTime, boolean closed) {
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.closed = closed;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}