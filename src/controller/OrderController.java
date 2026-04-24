package controller;

import model.Order;
import model.OrderItem;
import service.OrderManager;

import java.util.List;

public class OrderController {

    private OrderManager orderManager = new OrderManager();

    /**
     * GET /orders
     * Returns all active orders
     */
    public List<Order> getAllOrders() {
        return orderManager.getAllOrders();
    }

    /**
     * GET /orders/{id}
     * Returns a specific order by ID
     * Returns null if not found
     */
    public Order getOrderById(int id) {
        return orderManager.getOrderById(id);
    }

    /**
     * GET /orders/customer/{customerId}
     * Returns all orders placed by a specific customer
     */
    public List<Order> getOrdersByCustomer(int customerId) {
        return orderManager.getOrdersByCustomer(customerId);
    }

    /**
     * POST /orders
     * Creates a new order
     */
    public void createOrder(Order order) {
        orderManager.createOrder(order);
    }

    /**
     * POST /orders/{orderId}/items
     * Adds an item to an existing order
     */
    public void addItemToOrder(int orderId, OrderItem item) {
        orderManager.addItemToOrder(orderId, item);
    }

    /**
     * PUT /orders/{id}/status
     * Updates the status of an order
     * Possible values: Pending, Accepted, In Progress,
     *                  Ready for Collection, Collected, Cancelled
     */
    public void updateStatus(int id, String status) {
        orderManager.updateStatus(id, status);
    }

    /**
     * PUT /orders/{id}/cancel
     * Cancels an order with a reason
     * Possible reasons: "no_show" or "out_of_stock"
     */
    public void cancelOrder(int id, String reason) {
        orderManager.cancelOrder(id, reason);
    }

    /**
     * PUT /orders/{id}/archive
     * Moves a collected order to the archive
     */
    public void archiveOrder(int id) {
        orderManager.archiveOrder(id);
    }

    /**
     * GET /orders/archived
     * Returns all archived orders
     */
    public List<Order> getArchivedOrders() {
        return orderManager.getArchivedOrders();
    }
}
