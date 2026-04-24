package dto;

import model.MenuItem;

/**
 * Data Transfer Object (DTO) for MenuItem.
 * This ensures we only send safe, necessary data to the frontend (hiding internal flags like 'isDeleted').
 */
public class MenuItemResponse {
    public int id;
    public String name;
    public double priceRegular;
    public double priceLarge;
    public boolean available;

    /**
     * Factory method to convert a database entity (MenuItem) into a DTO.
     */
    public static MenuItemResponse from(MenuItem item) {
        MenuItemResponse res = new MenuItemResponse();
        res.id = item.getId();
        res.name = item.getName();
        res.priceRegular = item.getPriceRegular();
        res.priceLarge = item.getPriceLarge();
        res.available = item.isAvailable();
        return res;
    }
}