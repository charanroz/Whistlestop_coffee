package com.whistlestop_coffee.whistlestop_coffee.model;

/**
 * Represents a coffee or food item on the menu.
 * This acts as our core domain entity.
 */
public class MenuItem {
    private int id; // Unique identifier
    private String name; // Name of the item (e.g., "Latte")
    private boolean hasSize; // Indicates if the item comes in multiple sizes
    private double priceRegular; // Price for a regular cup
    private double priceLarge; // Price for a large cup
    private boolean isAvailable; // True if in stock, false if sold out
    private boolean isDeleted; // Soft delete flag to prevent breaking order history

    // Constructor
    public MenuItem(int id, String name, boolean hasSize, double priceRegular, double priceLarge) {
        this.id = id;
        this.name = name;
        this.hasSize = hasSize;
        this.priceRegular = priceRegular;
        this.priceLarge = priceLarge;
        this.isAvailable = true; // Default to available
        this.isDeleted = false;  // Default to not deleted
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public boolean hasSize() { return hasSize; }
    public double getPriceRegular() { return priceRegular; }
    public double getPriceLarge() { return priceLarge; }
    public boolean isAvailable() { return isAvailable; }
    public boolean isDeleted() { return isDeleted; }

    // Setters
    public void setAvailable(boolean available) { isAvailable = available; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public void setPriceRegular(double priceRegular) { this.priceRegular = priceRegular; }
    public void setPriceLarge(double priceLarge) {
        // Only update large price if the item actually supports multiple sizes
        if (this.hasSize) {
            this.priceLarge = priceLarge;
        }
    }
}