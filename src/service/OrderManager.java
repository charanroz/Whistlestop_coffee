package service;

import model.Order;
import java.util.List;
import java.util.ArrayList;

public class OrderManager {

    private List<Order> orders = new ArrayList<>();

    public void createOrder(Order order) {
        orders.add(order);
    }

    public List<Order> getOAllrders() {
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

    public void updateStatus(int id, String status) {
        Order order = getOrderById(id);
        if (order != null) {
            order.setStatus(status);
        }
    }

    public void cancelOrder(int id, String reason) {
        Order order = getOrderById(id);
        if (order != null) {
            order.setStatus("cancelled");
            order.setCancelReason(reason);
        }
    }

}
