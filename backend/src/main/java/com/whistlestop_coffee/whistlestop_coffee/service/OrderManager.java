package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.MenuItem;
import com.whistlestop_coffee.whistlestop_coffee.model.Order;
import com.whistlestop_coffee.whistlestop_coffee.model.OrderItem;
import com.whistlestop_coffee.whistlestop_coffee.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderManager {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuManager menuManager;

    private static final List<String> VALID_STATUSES = List.of(
            "Accepted", "In Progress", "Ready for Collection", "Collected", "Cancelled"
    );

    public void createOrder(Order order) {

        List<OrderItem> fixedItems = new ArrayList<>();

        for (OrderItem item : order.getItems()) {

            int menuItemId = item.getMenuItemId();

            MenuItem menuItem = menuManager.getMenuItemEntity(menuItemId);

            if (menuItem != null) {

                double price;

                if ("Large".equalsIgnoreCase(item.getSize())) {
                    price = menuItem.getPriceLarge();
                } else {
                    price = menuItem.getPriceRegular();
                }

                OrderItem newItem = new OrderItem(
                        menuItem,
                        item.getSize(),
                        item.getQuantity(),
                        java.math.BigDecimal.valueOf(price)
                );

                newItem.setOrder(order);

                fixedItems.add(newItem);

            } else {
                throw new RuntimeException("MenuItem not found for id: " + menuItemId);
            }
        }

        order.getItems().clear();
        order.getItems().addAll(fixedItems);

        order.setStatus("Accepted");

        orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(int id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Order> getOrdersByCustomer(int customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public void addItemToOrder(int orderId, OrderItem item) {
        Order order = getOrderById(orderId);
        if (order != null) {
            order.addItem(item);
            orderRepository.save(order);
        }
    }

    public boolean updateStatus(int id, String status) {
        if (!VALID_STATUSES.contains(status)) return false;

        Order order = getOrderById(id);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
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
        orderRepository.save(order);
        return true;
    }

    public void archiveOrder(int id) {
        Order order = getOrderById(id);
        if (order != null && order.getStatus().equals("Collected")) {
            order.setStatus("Archived");
            orderRepository.save(order);
        }
    }

    public List<Order> getArchivedOrders() {
        return orderRepository.findByStatus("Archived");
    }

    public List<Order> getActiveOrders() {
        return orderRepository.findByStatusNot("Archived");
    }
}