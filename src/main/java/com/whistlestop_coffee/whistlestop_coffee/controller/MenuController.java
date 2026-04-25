package com.whistlestop_coffee.whistlestop_coffee.controller;

import com.whistlestop_coffee.whistlestop_coffee.dto.MenuItemResponse;
import com.whistlestop_coffee.whistlestop_coffee.service.MenuManager;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/menu")
@CrossOrigin(origins = "*")
public class MenuController {
    private MenuManager menuManager = new MenuManager();

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
    }

    @DeleteMapping("/{id}")
    public void deleteMenuItem(@PathVariable int id) {
        menuManager.deleteMenuItem(id);
    }
}