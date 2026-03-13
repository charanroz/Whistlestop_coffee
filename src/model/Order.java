package model;

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

    public int getId() {
        return id;
    }
    public Customer getCustomer() {
        return customer;
    }
    public String getPickupTime() {
        return pickupTime;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getCancelReason() {
        return cancelReason;
    }
    public void setCancelReason(String reason) {
        this.cancelReason = reason;
    }
    public List<OrderItem> getItems() {
        return items;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customer=" + customer.getName() +
                ", pickupTime='" + pickupTime + '\'' +
                ", status='" + status + '\'' +
                ", items=" + items +
                '}';
    }
}