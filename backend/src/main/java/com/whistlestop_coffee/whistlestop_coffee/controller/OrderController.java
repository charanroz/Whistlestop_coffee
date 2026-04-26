package com.whistlestop_coffee.whistlestop_coffee.controller;

import com.whistlestop_coffee.whistlestop_coffee.model.Order;
import com.whistlestop_coffee.whistlestop_coffee.model.OrderItem;
import com.whistlestop_coffee.whistlestop_coffee.service.OrderManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.whistlestop_coffee.whistlestop_coffee.dto.OrderResponse;


import java.util.stream.Collectors;

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
    public ResponseEntity<String> updateStatus(@PathVariable int id, @RequestParam String status) {
        boolean success = orderManager.updateStatus(id, status);
        if (!success) {
            return ResponseEntity.badRequest().body("Invalid status or order not found");
        }
        return ResponseEntity.ok("Status updated successfully");
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable int id, @RequestParam String reason) {
        boolean success = orderManager.cancelOrder(id, reason);
        if (!success) {
            return ResponseEntity.badRequest().body("Cannot cancel order — order not found or 15 minutes have not passed yet");
        }
        return ResponseEntity.ok("Order cancelled successfully");
    }

    @PutMapping("/{id}/archive")
    public void archiveOrder(@PathVariable int id) {
        orderManager.archiveOrder(id);
    }

    @GetMapping("/archived")
    public List<Order> getArchivedOrders() {
        return orderManager.getArchivedOrders();
    }

    @GetMapping("/staff/active")
    public List<OrderResponse> getActiveOrdersForStaff() {
        // orderManager.getAllOrders() 默认返回的也就是未被 archive 的订单
        List<Order> activeOrders = orderManager.getAllOrders();

        // 将后端实体批量转换为前端需要的 DTO 格式
        return activeOrders.stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }
}