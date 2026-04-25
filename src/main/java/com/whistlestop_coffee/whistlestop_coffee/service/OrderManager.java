package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.Order;
import com.whistlestop_coffee.whistlestop_coffee.model.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderManager {
    // List of all active orders currently in the system
    private List<Order> orders = new ArrayList<>();
    // List of completed orders that have been archived
    private List<Order> archivedOrders = new ArrayList<>();

    /**
     * Creates a new order and adds it to the active orders list
     * @param order The order to be added
     */
    // Create a new order
    public void createOrder(Order order) {
        orders.add(order);
    }

    /**
     * Returns all active orders currently in the system
     * @return List of active orders
     */
    // Get all active orders
    public List<Order> getAllOrders() {
        return orders;
    }
    /**
     * Finds and returns an order by its ID
     * Returns null if no order with the given ID is found
     * @param id The order ID to search for
     * @return The matching Order, or null if not found
     */
    // Find order by ID
    public Order getOrderById(int id) {
        for (Order order : orders) {
            if (order.getId() == id) {
                return order;
            }
        }
        return null;
    }
    /**
     * Returns all orders placed by a specific customer
     * @param customerId The ID of the customer
     * @return List of orders belonging to the customer
     */
    // Get all orders placed by a specific customer
    public List<Order> getOrdersByCustomer(int customerId) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders) {
            if (order.getCustomer().getId() == customerId) {
                result.add(order);
            }
        }
        return result;
    }

    /**
     * Adds an item to an existing order
     * Does nothing if the order is not found
     * @param orderId The ID of the order to add the item to
     * @param item The OrderItem to add
     */

    // Add an item to an existing order
    public void addItemToOrder(int orderId, OrderItem item) {
        Order order = getOrderById(orderId);
        if (order != null) {
            order.addItem(item);
        }
    }

    /**
     * Updates the status of an order
     * Status flow: Pending -> Accepted -> In Progress
     *           -> Ready for Collection -> Collected -> Cancelled
     * @param id The order ID
     * @param status The new status to set
     */

    public void updateStatus(int id, String status) {
        Order order = getOrderById(id);
        if (order != null) {
            order.setStatus(status);
        }
    }
    /**
     * Cancels an order and records the reason
     * Possible reasons: "no_show" (customer did not collect within 15 minutes)
     *                   "out_of_stock" (kiosk ran out of the ordered item)
     * @param id The order ID to cancel
     * @param reason The reason for cancellation
     */
    // Cancel an order with a reason
    public void cancelOrder(int id, String reason) {
        Order order = getOrderById(id);
        if (order != null) {
            order.setStatus("Cancelled");
            order.setCancelReason(reason);
        }
    }
    /**
     * Moves a collected order to the archive
     * Only archives orders with status "Collected"
     * Does nothing if the order is not found or not yet collected
     * @param id The order ID to archive
     */

    // Move a collected order to the archive
    public void archiveOrder(int id) {
        Order order = getOrderById(id);
        if (order != null && order.getStatus().equals("Collected")) {
            orders.remove(order);
            archivedOrders.add(order);
        }
    }
    /**
     * Returns all archived orders
     * @return List of archived orders
     */
    // Get all archived orders
    public List<Order> getArchivedOrders() {
        return archivedOrders;
    }
}