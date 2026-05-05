package com.whistlestop_coffee.whistlestop_coffee.controller;

import com.whistlestop_coffee.whistlestop_coffee.dto.MenuItemResponse;
import com.whistlestop_coffee.whistlestop_coffee.service.MenuManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// This controller receives HTTP requests from the React frontend and passes them to the MenuManager.
@RestController
@RequestMapping("/menu")
// I added this @CrossOrigin because I was getting CORS blocking errors when testing with the frontend.
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MenuController {

    @Autowired
    private MenuManager menuManager;

    // Default route for customers (only shows available items)
    @GetMapping
    public List<MenuItemResponse> getAvailableMenu() {
        return menuManager.getAvailableMenu();
    }

    // Route for staff (shows all items including out of stock, but not deleted ones)
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
        // Just printing to console for now to help me debug if the request works
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

    // Added this route to fulfill the "edit existing menu items" requirement.
    // It takes a new string and updates the database.
    @PutMapping("/{id}/name")
    public void updateItemName(@PathVariable int id, @RequestParam String newName) {
        menuManager.updateMenuItemName(id, newName);
        // Printing this to the console so I can check if the frontend is actually sending the right string when I click save
        System.out.println("Fixed typo for item " + id + ", new name is: " + newName);
    }
}