package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.Order;
import com.whistlestop_coffee.whistlestop_coffee.model.OrderItem;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class OrderManager {
    private List<Order> orders = new ArrayList<>();
    private List<Order> archivedOrders = new ArrayList<>();

    private static final List<String> VALID_STATUSES = List.of(
            "Accepted", "In Progress", "Ready for Collection", "Collected", "Cancelled"
    );

    public void createOrder(Order order) {
        orders.add(order);
    }

    public List<Order> getAllOrders() {
        return orders;
    }

    public Order getOrderById(int id) {
        for (Order order : orders) {
            if (order.getId() == id) {
                return order;
            }
        }
        return null;
    }

    public List<Order> getOrdersByCustomer(int customerId) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders) {
            if (order.getCustomer().getId() == customerId) {
                result.add(order);
            }
        }
        return result;
    }

    public void addItemToOrder(int orderId, OrderItem item) {
        Order order = getOrderById(orderId);
        if (order != null) {
            order.addItem(item);
        }
    }

    public boolean updateStatus(int id, String status) {
        if (!VALID_STATUSES.contains(status)) {
            return false;
        }
        Order order = getOrderById(id);
        if (order != null) {
            order.setStatus(status);
            return true;
        }
        return false;
    }

    public boolean cancelOrder(int id, String reason) {
        Order order = getOrderById(id);
        if (order == null) return false;

        if (reason.equals("no_show")) {
            LocalTime pickupTime = LocalTime.parse(order.getPickupTime());
            LocalTime cutoff = pickupTime.plusMinutes(15);
            if (LocalTime.now().isBefore(cutoff)) {
                return false;
            }
        }

        order.setStatus("Cancelled");
        order.setCancelReason(reason);
        return true;
    }

    public void archiveOrder(int id) {
        Order order = getOrderById(id);
        if (order != null && order.getStatus().equals("Collected")) {
            orders.remove(order);
            archivedOrders.add(order);
        }
    }

    public List<Order> getArchivedOrders() {
        return archivedOrders;
    }
}