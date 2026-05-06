package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.*;
import com.whistlestop_coffee.whistlestop_coffee.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Service
public class OrderManager {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    // ✅ FIX: add this (important)
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BusinessHourManager businessHourManager;

    private final List<String> validStatuses = Arrays.asList(
            "Pending",
            "Accepted",
            "In Progress",
            "Ready for Collection",
            "Collected",
            "Cancelled"
    );

    // 📦 ARCHIVE instead of DELETE
    @Scheduled(cron = "0 0 23 * * ?")
    public void archiveCompletedOrders() {

        List<Order> orders = orderRepository.findByStatusIn(
                Arrays.asList("Collected", "Cancelled")
        );

        for (Order order : orders) {
            order.setArchived(true);
        }

        orderRepository.saveAll(orders);

        System.out.println("📦 Archived collected & cancelled orders");
    }

    // ✅ CREATE ORDER (FIXED)
    public Order createOrder(Order order) {

        Customer dbCustomer = customerRepository
                .findById(order.getCustomer().getId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Validate Business Hours
        String timeStr = order.getPickupTime();
        if (timeStr != null) {
            String timeOnly = timeStr;
            String dayOfWeek = java.time.LocalDate.now().getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);
            
            if (timeStr.contains(" ")) {
                String[] parts = timeStr.split(" ");
                try {
                    dayOfWeek = java.time.LocalDate.parse(parts[0]).getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);
                    timeOnly = parts[1];
                } catch (Exception e) {
                    timeOnly = parts[1];
                }
            }
            if (!businessHourManager.isWithinBusinessHours(dayOfWeek, timeOnly)) {
                throw new RuntimeException("outofbusinesshours");
            }
        }

        order.setCustomer(dbCustomer);
        order.setStatus("Pending");

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : order.getItems()) {

            Integer menuItemId = item.getMenuItemId();

            MenuItem menuItem = menuItemRepository.findById(menuItemId)
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));

            item.setMenuItem(menuItem);

            // ✅ MOVE THIS INSIDE LOOP
            BigDecimal price;

            if ("Large".equalsIgnoreCase(item.getSize())) {
                price = BigDecimal.valueOf(menuItem.getPriceLarge());
            } else {
                price = BigDecimal.valueOf(menuItem.getPriceRegular());
            }

            item.setUnitPrice(price);

            BigDecimal itemTotal = price.multiply(
                    BigDecimal.valueOf(item.getQuantity())
            );

            total = total.add(itemTotal);

            item.setOrder(order);
        }

        order.setTotal(total);

        return orderRepository.save(order);
    }

    // ➕ ADD ITEM TO ORDER
    public void addItemToOrder(int orderId, OrderItem item) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        MenuItem menuItem = menuItemRepository.findById(item.getMenuItemId())
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        item.setMenuItem(menuItem);

        BigDecimal price;

        if ("Large".equalsIgnoreCase(item.getSize())) {
            price = BigDecimal.valueOf(menuItem.getPriceLarge());
        } else {
            price = BigDecimal.valueOf(menuItem.getPriceRegular());
        }

        item.setUnitPrice(price);
        item.setOrder(order);

        order.getItems().add(item);

        BigDecimal total = order.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotal(total);

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

    public List<Order> getActiveOrders() {
        return orderRepository.findByArchivedFalse()
                .stream()
                .filter(o -> !o.getStatus().equals("Collected") &&
                        !o.getStatus().equals("Cancelled"))
                .toList();
    }

    public List<Order> getArchivedOrders() {
        return orderRepository.findByArchivedTrue();
    }


    public boolean updateStatus(int id, String status) {
        if (!validStatuses.contains(status)) return false;

        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return false;

        order.setStatus(status);
        orderRepository.save(order);
        return true;
    }

    public boolean cancelOrder(int id, String reason) {
        System.out.println("🚨 [DEBUG] 收到取消請求！訂單ID: " + id + ", 傳來的原因是: [" + reason + "]");
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) return false;

        try {
            LocalTime pickup = LocalTime.parse(order.getPickupTime());
            LocalTime now = LocalTime.now();

            if (now.isBefore(pickup.plusMinutes(15))) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        order.setStatus("Cancelled");
        order.setCancelReason(reason);
        orderRepository.save(order);

        return true;
    }


    public void staffCancelOrder(int id, String reason) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus("Cancelled");
        order.setCancelReason(reason);
        //confirm customer is late more than 15mins
        if ("CUSTOMER_LATE".equals(reason)) {
            try {
                String timeStr = order.getPickupTime();
                String timeOnly = timeStr;
                if (timeStr != null && timeStr.contains(" ")) {
                    timeOnly = timeStr.split(" ")[1];
                }

                LocalTime pickup = LocalTime.parse(timeOnly);
                LocalTime now = LocalTime.now();

                // It cannot be canceled less than 15mins
                if (now.isBefore(pickup.plusMinutes(15))) {
                    throw new RuntimeException("Cannot cancel: 15 minutes have not passed.");
                }

            } catch (RuntimeException re) {
                throw re; // return to Controller
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Unable to verify the time");
            }
        }
        //Order can be saved due to verify the time(>15mins) or out of stock
        order.setStatus("Cancelled");
        order.setCancelReason(reason);

        orderRepository.save(order);
    }


    public void archiveOrder(int id) {

        Order order = orderRepository.findById(id).orElse(null);

        if (order != null) {
            order.setArchived(true);
            orderRepository.save(order);
        }
    }
}