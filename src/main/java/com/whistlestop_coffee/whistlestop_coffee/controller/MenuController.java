package com.whistlestop_coffee.whistlestop_coffee.controller;

import com.whistlestop_coffee.whistlestop_coffee.dto.MenuItemResponse;
import com.whistlestop_coffee.whistlestop_coffee.service.MenuManager;
import java.util.List;

/**
 * API Layer. Simulates REST endpoints to handle incoming requests.
 */
public class MenuController {
    private MenuManager menuManager = new MenuManager();

    /**
     * GET /menu
     * Fetches available menu for customers.
     */
    public List<MenuItemResponse> getAvailableMenu() {
        return menuManager.getAvailableMenu();
    }

    /**
     * GET /menu/all
     * Fetches the full menu for staff (includes out-of-stock items).
     */
    public List<MenuItemResponse> getFullMenu() {
        return menuManager.getFullMenu();
    }

    /**
     * GET /menu/{id}
     * Fetches details of a specific item.
     */
    public MenuItemResponse getById(int id) {
        return menuManager.getById(id);
    }

    /**
     * PUT /menu/{id}/availability
     * Updates the stock status of an item.
     */
    public void setAvailability(int id, boolean available) {
        menuManager.updateAvailability(id, available);
        System.out.println("Item " + id + " availability changed to: " + available);
    }

    /**
     * DELETE /menu/{id}
     * Logically deletes an item from the menu.
     */
    public void deleteMenuItem(int id) {
        menuManager.deleteMenuItem(id);
        System.out.println("Item " + id + " has been logically deleted.");
    }
}