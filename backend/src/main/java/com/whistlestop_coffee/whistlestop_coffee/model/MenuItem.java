package com.whistlestop_coffee.whistlestop_coffee.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private boolean hasSize;
    private double priceRegular;
    private double priceLarge;
    private boolean isAvailable;
    private boolean isDeleted;

    public MenuItem() {}

    public MenuItem(int id, String name, boolean hasSize, double priceRegular, double priceLarge) {
        this.id = id;
        this.name = name;
        this.hasSize = hasSize;
        this.priceRegular = priceRegular;
        this.priceLarge = priceLarge;
        this.isAvailable = true;
        this.isDeleted = false;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public boolean hasSize() { return hasSize; }
    public double getPriceRegular() { return priceRegular; }
    public double getPriceLarge() { return priceLarge; }
    public boolean isAvailable() { return isAvailable; }
    public boolean isDeleted() { return isDeleted; }

    public void setAvailable(boolean available) { isAvailable = available; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public void setPriceRegular(double priceRegular) { this.priceRegular = priceRegular; }
    public void setPriceLarge(double priceLarge) {
        if (this.hasSize) {
            this.priceLarge = priceLarge;
        }
    }
}