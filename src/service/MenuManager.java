package service;

import model.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class MenuManager {

    private List<MenuItem> menuItems = new ArrayList<>();

    public MenuManager() {
        loadDefaultMenu();
    }

    private void loadDefaultMenu() {
        menuItems.add(new MenuItem(1, "Americano", true, 1.50, 2.00));
        menuItems.add(new MenuItem(2, "Americano with milk", true, 2.00, 2.50));
        menuItems.add(new MenuItem(3, "Latte", true, 2.50, 3.00));
        menuItems.add(new MenuItem(4, "Cappuccino", true, 2.50, 3.00));
        menuItems.add(new MenuItem(5, "Hot Chocolate", true, 2.00, 2.50));
        menuItems.add(new MenuItem(6, "Mocha", true, 2.50, 3.00));
        // Mineral Water hasSize is false, large price is set to 0.0 but unused
        menuItems.add(new MenuItem(7, "Mineral Water", false, 1.00, 0.00));
    }

    public List<MenuItem> getAllMenuItems() {
        return menuItems;
    }

    public MenuItem getMenuItemById(int id) {
        for (MenuItem item : menuItems) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public void updateItemAvailability(int id, boolean isAvailable) {
        MenuItem item = getMenuItemById(id);
        if (item != null) {
            item.setAvailable(isAvailable);
        }
    }

    public void addMenuItem(MenuItem newItem) {
        menuItems.add(newItem);
    }
}