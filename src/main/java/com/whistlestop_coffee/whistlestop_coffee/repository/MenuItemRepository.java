package repository;

import model.MenuItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Acts as an in-memory database for MenuItems.
 * Replaces actual database queries until Spring Boot/SQL is implemented.
 */
public class MenuItemRepository {
    private List<MenuItem> menuItems = new ArrayList<>();

    public MenuItemRepository() {
        // Initialize default menu data (Simulating database seed)
        menuItems.add(new MenuItem(1, "Americano", true, 1.50, 2.00));
        menuItems.add(new MenuItem(2, "Americano with milk", true, 2.00, 2.50));
        menuItems.add(new MenuItem(3, "Latte", true, 2.50, 3.00));
        menuItems.add(new MenuItem(4, "Cappuccino", true, 2.50, 3.00));
        menuItems.add(new MenuItem(5, "Hot Chocolate", true, 2.00, 2.50));
        menuItems.add(new MenuItem(6, "Mocha", true, 2.50, 3.00));
        // Mineral Water has no large size, so large price is 0.00 and hasSize is false
        menuItems.add(new MenuItem(7, "Mineral Water", false, 1.00, 0.00));
    }

    /**
     * Retrieves all items that have not been logically deleted.
     */
    public List<MenuItem> findActiveItems() {
        List<MenuItem> active = new ArrayList<>();
        for (MenuItem item : menuItems) {
            if (!item.isDeleted()) {
                active.add(item);
            }
        }
        return active;
    }

    /**
     * Finds a specific menu item by its ID.
     * Returns null if not found or if the item is logically deleted.
     */
    public MenuItem findById(int id) {
        for (MenuItem item : menuItems) {
            if (item.getId() == id && !item.isDeleted()) {
                return item;
            }
        }
        return null; // Not found
    }
}