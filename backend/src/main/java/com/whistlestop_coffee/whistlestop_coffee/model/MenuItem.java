package com.whistlestop_coffee.whistlestop_coffee.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// This class maps to the menu_item table in our database.
@Entity
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private boolean hasSize;
    private double priceRegular;

    // Only used if hasSize is true.
    private double priceLarge;

    private boolean isAvailable;

    // I added this flag for "soft delete".
    // If we actually delete a menu item from the database, past orders might break.
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

    // I had to add this setter later. I realized if the staff makes a typo when adding a new coffee,
    // we need a way to edit the name without deleting the whole item.
    public void setName(String name) {
        this.name = name;
    }
}