package service;

import model.Customer;
import model.Order;
import model.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderManager {

    private List<Order> orders = new ArrayList<>();
    private List<Order> archivedOrders = new ArrayList<>();


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


    public void updateStatus(int id, String status) {
        Order order = getOrderById(id);
        if (order != null) {
            order.setStatus(status);
        }
    }


    public void cancelOrder(int id, String reason) {
        Order order = getOrderById(id);
        if (order != null) {
            order.setStatus("Cancelled");
            order.setCancelReason(reason);
        }
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