package com.whistlestop_coffee.whistlestop_coffee.dto;

import com.whistlestop_coffee.whistlestop_coffee.model.MenuItem;

/**
 * DTO used to transfer menu data to the frontend.
 * Decouples the API from the database schema to hide internal flags like 'isDeleted'.
 */
public class MenuItemResponse {
    public int id;
    public String name;
    public double priceRegular;
    public double priceLarge;
    public boolean available;
    public boolean hasSize;

    /**
     * Static factory method to map Entity to DTO.
     * Ensures the UI only receives data it actually needs to render.
     */
    public static MenuItemResponse from(MenuItem item) {
        MenuItemResponse res = new MenuItemResponse();
        res.id = item.getId();
        res.name = item.getName();
        res.priceRegular = item.getPriceRegular();
        res.priceLarge = item.getPriceLarge();
        res.available = item.isAvailable();
        res.hasSize = item.hasSize();
        return res;
    }
}