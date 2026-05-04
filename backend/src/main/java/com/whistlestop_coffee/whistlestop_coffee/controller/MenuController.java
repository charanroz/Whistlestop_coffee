package com.whistlestop_coffee.whistlestop_coffee.controller;

import com.whistlestop_coffee.whistlestop_coffee.dto.MenuItemResponse;
import com.whistlestop_coffee.whistlestop_coffee.service.MenuManager;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

/**
 * API Layer. Simulates REST endpoints to handle incoming requests.
 */
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/menu")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MenuController {
    @Autowired
    private MenuManager menuManager;

    @GetMapping
    public List<MenuItemResponse> getAvailableMenu() {
        return menuManager.getAvailableMenu();
    }

    @GetMapping("/all")
    public List<MenuItemResponse> getFullMenu() {
        return menuManager.getFullMenu();
    }

    @GetMapping("/{id}")
    public MenuItemResponse getById(@PathVariable int id) {
        return menuManager.getById(id);
    }

    @PutMapping("/{id}/availability")
    public void setAvailability(@PathVariable int id, @RequestParam boolean available) {
        menuManager.updateAvailability(id, available);
        System.out.println("Item " + id + " availability changed to: " + available);
    }

    @DeleteMapping("/{id}")
    public void deleteMenuItem(@PathVariable int id) {
        menuManager.deleteMenuItem(id);
        System.out.println("Item " + id + " has been logically deleted.");
    }

    @PostMapping
    public void addMenuItem(@RequestBody com.whistlestop_coffee.whistlestop_coffee.model.MenuItem newItem) {
        menuManager.addMenuItem(newItem);
        System.out.println("New item added: " + newItem.getName());
    }

    @PutMapping("/{id}/price")
    public void updatePrices(@PathVariable int id,
                             @RequestParam double priceRegular,
                             @RequestParam double priceLarge) {
        menuManager.updatePrices(id, priceRegular, priceLarge);
        System.out.println("Item " + id + " prices updated to Regular: " + priceRegular + ", Large: " + priceLarge);
    }
}