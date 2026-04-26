package com.whistlestop_coffee.whistlestop_coffee.model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private Customer customer;
    private String pickupTime;
    private String status;
    private String cancelReason;
    private List<OrderItem> items;

    public Order(int id, Customer customer, String pickupTime) {
        this.id = id;
        this.customer = customer;
        this.pickupTime = pickupTime;
        this.status = "Pending";
        this.items = new ArrayList<>();
    }
    // Returns the unique ID of this order
    public int getId() {
        return id;
    }
    // Returns the Customer who placed this order
    public Customer getCustomer() {
        return customer;
    }
    // Returns the pickup time selected by the customer e.g. "14:00", "14:30"
    public String getPickupTime() {
        return pickupTime;
    }
    // Returns the current status of the order
    // Possible values: Pending, Accepted, In Progress, Ready for Collection, Collected, Cancelled
    public String getStatus() {
        return status;
    }
    // Updates the status of the order
// Possible values: Pending, Accepted, In Progress, Ready for Collection, Collected, Cancelled
    public void setStatus(String status) {
        this.status = status;
    }
    // Returns the reason why the order was cancelled
// Possible values: "no_show" (customer did not collect within 15 minutes)
//                  "out_of_stock" (kiosk ran out of the ordered item)
    public String getCancelReason() {
        return cancelReason;
    }
    // Sets the reason for cancelling the order
// Possible values: "no_show" or "out_of_stock"
    public void setCancelReason(String reason) {
        this.cancelReason = reason;
    }
    // Returns the list of all OrderItems in this order
    public List<OrderItem> getItems() {
        return items;
    }

    // Adds a new OrderItem to this order
    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    // Calculates and returns the total price of all items in this order
// Total is calculated by summing the subtotal of each OrderItem
    public double getTotalPrice() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getSubtotal().doubleValue();
        }
        return total;
    }







    // Returns a string representation of this order
// Includes: id, customer name, pickupTime, status, totalPrice, items

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customer=" + customer.getName() +
                ", pickupTime='" + pickupTime + '\'' +
                ", status='" + status + '\'' +
                ", totalPrice=" + getTotalPrice() +
                ", items=" + items +
                '}';
    }
}