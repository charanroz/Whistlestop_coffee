package service;

import model.Order;
import model.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderManager {

    private List<Order> orders = new ArrayList<>();
    private List<Order> archivedOrders = new ArrayList<>();

    // Create a new order
    public void createOrder(Order order) {
        orders.add(order);
    }

    // Get all active orders
    public List<Order> getAllOrders() {
        return orders;
    }

    // Find order by ID
    public Order getOrderById(int id) {
        for (Order order : orders) {
            if (order.getId() == id) {
                return order;
            }
        }
        return null;
    }

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

    // Add an item to an existing order
    public void addItemToOrder(int orderId, OrderItem item) {
        Order order = getOrderById(orderId);
        if (order != null) {
            order.addItem(item);
        }
    }

    // Update order status
    // Pending -> Accepted -> In Progress
    // -> Ready for Collection -> Collected -> Cancelled
    public void updateStatus(int id, String status) {
        Order order = getOrderById(id);
        if (order != null) {
            order.setStatus(status);
        }
    }

    // Cancel an order with a reason
    public void cancelOrder(int id, String reason) {
        Order order = getOrderById(id);
        if (order != null) {
            order.setStatus("Cancelled");
            order.setCancelReason(reason);
        }
    }

    // Move a collected order to the archive
    public void archiveOrder(int id) {
        Order order = getOrderById(id);
        if (order != null && order.getStatus().equals("Collected")) {
            orders.remove(order);
            archivedOrders.add(order);
        }
    }

    // Get all archived orders
    public List<Order> getArchivedOrders() {
        return archivedOrders;
    }
}