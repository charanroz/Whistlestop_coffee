package com.whistlestop_coffee.whistlestop_coffee.controller;

import com.whistlestop_coffee.whistlestop_coffee.model.Order;
import com.whistlestop_coffee.whistlestop_coffee.model.OrderItem;
import com.whistlestop_coffee.whistlestop_coffee.service.OrderManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderManager orderManager;

    public OrderController(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    //  GET ALL
    @GetMapping
    public List<Order> getAllOrders() {
        return orderManager.getAllOrders();
    }

    //  GET BY ID
    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable int id) {
        return orderManager.getOrderById(id);
    }

    // GET BY CUSTOMER
    @GetMapping("/customer/{customerId}")
    public List<Order> getOrdersByCustomer(@PathVariable int customerId) {
        return orderManager.getOrdersByCustomer(customerId);
    }

    //  CREATE ORDER
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            Order created = orderManager.createOrder(order);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //  ADD ITEM
    @PostMapping("/{orderId}/items")
    public void addItemToOrder(@PathVariable int orderId,
                               @RequestBody OrderItem item) {
        orderManager.addItemToOrder(orderId, item);
    }

    //  UPDATE STATUS
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable int id,
                                               @RequestParam String status) {
        boolean success = orderManager.updateStatus(id, status);
        if (!success) {
            return ResponseEntity.badRequest()
                    .body("Invalid status or order not found");
        }
        return ResponseEntity.ok("Status updated successfully");
    }

    //  CUSTOMER CANCEL
    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable int id,
                                              @RequestParam String reason) {
        boolean success = orderManager.cancelOrder(id, reason);
        if (!success) {
            return ResponseEntity.badRequest()
                    .body("Cannot cancel order yet");
        }
        return ResponseEntity.ok("Order cancelled successfully");
    }

    //  STAFF CANCEL
    @PutMapping("/{id}/staff-cancel")
    public ResponseEntity<String> staffCancelOrder(@PathVariable int id,
                                                   @RequestParam String reason) {
        try {
            orderManager.staffCancelOrder(id, reason);
            return ResponseEntity.ok("Order cancelled by staff");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //  ARCHIVE
    @PutMapping("/{id}/archive")
    public void archiveOrder(@PathVariable int id) {
        orderManager.archiveOrder(id);
    }

    //  ACTIVE ORDERS
    @GetMapping("/staff/active")
    public List<Order> getActiveOrders() {
        return orderManager.getActiveOrders();
    }

    //  ARCHIVED
    @GetMapping("/archived")
    public List<Order> getArchivedOrders() {
        return orderManager.getArchivedOrders();
    }
}