package service;

import dto.MenuItemResponse;
import model.MenuItem;
import repository.MenuItemRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * Business logic layer for managing the Menu.
 */
public class MenuManager {

    // Data access layer dependency
    private MenuItemRepository repository = new MenuItemRepository();

    /**
     * For Customers: Returns only the menu items that are currently in stock.
     */
    public List<MenuItemResponse> getAvailableMenu() {
        List<MenuItemResponse> result = new ArrayList<>();
        for (MenuItem item : repository.findActiveItems()) {
            if (item.isAvailable()) {
                result.add(MenuItemResponse.from(item));
            }
        }
        return result;
    }

    /**
     * For Staff/Dashboard: Returns all active items, including those out of stock.
     */
    public List<MenuItemResponse> getFullMenu() {
        List<MenuItemResponse> result = new ArrayList<>();
        for (MenuItem item : repository.findActiveItems()) {
            result.add(MenuItemResponse.from(item));
        }
        return result;
    }

    /**
     * For API/Frontend: Retrieves a single item as a DTO.
     */
    public MenuItemResponse getById(int id) {
        MenuItem item = repository.findById(id);
        if (item != null) {
            return MenuItemResponse.from(item);
        }
        return null;
    }

    /**
     * IMPORTANT: For Internal Team Use (OrderManager).
     * Order module needs the actual Entity to calculate prices and bind to orders.
     * Do not return DTO here.
     */
    public MenuItem getMenuItemEntity(int id) {
        return repository.findById(id);
    }

    /**
     * For Staff: Mark an item as sold out or back in stock.
     */
    public void updateAvailability(int id, boolean available) {
        MenuItem item = repository.findById(id);
        if (item != null) {
            item.setAvailable(available);
        }
    }

    /**
     * For Staff: Permanently remove an item from the menu (Soft Delete).
     * We use soft delete to ensure past orders referencing this item do not break.
     */
    public void deleteMenuItem(int id) {
        MenuItem item = repository.findById(id);
        if (item != null) {
            item.setDeleted(true);    // Mark as deleted
            item.setAvailable(false); // Make it automatically out of stock
        }
    }
}