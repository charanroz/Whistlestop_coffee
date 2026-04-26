package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.dto.MenuItemResponse;
import com.whistlestop_coffee.whistlestop_coffee.model.MenuItem;
import com.whistlestop_coffee.whistlestop_coffee.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuManager {

    @Autowired
    private MenuItemRepository repository;

    public List<MenuItemResponse> getAvailableMenu() {
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
                .orElse(null);
    }

    public MenuItem getMenuItemEntity(int id) {
        return repository.findById(id).orElse(null);
    }

    public void updateAvailability(int id, boolean available) {
        repository.findById(id).ifPresent(item -> {
            item.setAvailable(available);
            repository.save(item);
        });
    }

    public void deleteMenuItem(int id) {
        repository.findById(id).ifPresent(item -> {
            item.setDeleted(true);
            item.setAvailable(false);
            repository.save(item);
        });
    }
}