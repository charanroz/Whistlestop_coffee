package com.whistlestop_coffee.whistlestop_coffee.controller;

import com.whistlestop_coffee.whistlestop_coffee.model.Order;
import com.whistlestop_coffee.whistlestop_coffee.model.OrderItem;
import com.whistlestop_coffee.whistlestop_coffee.service.OrderManager;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private OrderManager orderManager = new OrderManager();

    @GetMapping
    public List<Order> getAllOrders() {
        return orderManager.getAllOrders();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable int id) {
        return orderManager.getOrderById(id);
    }

    @GetMapping("/customer/{customerId}")
    public List<Order> getOrdersByCustomer(@PathVariable int customerId) {
        return orderManager.getOrdersByCustomer(customerId);
    }

    @PostMapping
    public void createOrder(@RequestBody Order order) {
        orderManager.createOrder(order);
    }

    @PostMapping("/{orderId}/items")
    public void addItemToOrder(@PathVariable int orderId, @RequestBody OrderItem item) {
        orderManager.addItemToOrder(orderId, item);
    }

    @PutMapping("/{id}/status")
    public void updateStatus(@PathVariable int id, @RequestParam String status) {
        orderManager.updateStatus(id, status);
    }

    @PutMapping("/{id}/cancel")
    public void cancelOrder(@PathVariable int id, @RequestParam String reason) {
        orderManager.cancelOrder(id, reason);
    }

    @PutMapping("/{id}/archive")
    public void archiveOrder(@PathVariable int id) {
        orderManager.archiveOrder(id);
    }

    @GetMapping("/archived")
    public List<Order> getArchivedOrders() {
        return orderManager.getArchivedOrders();
    }
}