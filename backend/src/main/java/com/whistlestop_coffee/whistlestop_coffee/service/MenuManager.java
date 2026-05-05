package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.dto.MenuItemResponse;
import com.whistlestop_coffee.whistlestop_coffee.model.MenuItem;
import com.whistlestop_coffee.whistlestop_coffee.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// This class handles the main business logic.
// Keeping this separate from the Controller makes the code cleaner.
@Service
public class MenuManager {

    @Autowired
    private MenuItemRepository repository;

    public List<MenuItemResponse> getAvailableMenu() {
        // Find items that are not deleted and in stock, then convert them to DTOs
        return repository.findByIsAvailableTrueAndIsDeletedFalse()
                .stream()
                .map(MenuItemResponse::from)
                .collect(Collectors.toList());
    }

    public List<MenuItemResponse> getFullMenu() {
        return repository.findByIsDeletedFalse()
                .stream()
                .map(MenuItemResponse::from)
                .collect(Collectors.toList());
    }

    public MenuItemResponse getById(int id) {
        return repository.findById(id)
                .map(MenuItemResponse::from)
                .orElse(null); // Return null if not found
    }

    public MenuItem getMenuItemEntity(int id) {
        return repository.findById(id).orElse(null);
    }

    public void updateAvailability(int id, boolean available) {
        // Optional is used here to avoid NullPointerException if the item doesn't exist
        repository.findById(id).ifPresent(item -> {
            item.setAvailable(available);
            repository.save(item);
        });
    }

    public void deleteMenuItem(int id) {
        repository.findById(id).ifPresent(item -> {
            // We just update the flags instead of actually deleting the row from database
            item.setDeleted(true);
            item.setAvailable(false); // Also make it out of stock just in case
            repository.save(item);
        });
    }

    public void addMenuItem(MenuItem newItem) {
        newItem.setDeleted(false); // Ensure new items are not marked as deleted
        repository.save(newItem);
    }

    public void updatePrices(int id, double priceRegular, double priceLarge) {
        repository.findById(id).ifPresent(item -> {
            item.setPriceRegular(priceRegular);
            item.setPriceLarge(priceLarge);
            repository.save(item);
        });
    }

    // The requirements specifically said we need to "edit existing items".
    // This method allows us to fix typos in the item name.
    public void updateMenuItemName(int id, String newName) {
        repository.findById(id).ifPresent(item -> {
            item.setName(newName);
            repository.save(item);
        });
    }
}