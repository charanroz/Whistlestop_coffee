package com.whistlestop_coffee.whistlestop_coffee.dto;

import com.whistlestop_coffee.whistlestop_coffee.model.MenuItem;

// I learned that we shouldn't send the raw database entity directly to the frontend.
// This is a DTO class so we only send the safe data the customer needs to see,
// keeping internal flags like 'isDeleted' hidden.
public class MenuItemResponse {
    public int id;
    public String name;
    public double priceRegular;
    public double priceLarge;
    public boolean available;
    public boolean hasSize;

    // Helper method to copy data from the MenuItem database entity into this DTO object.
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