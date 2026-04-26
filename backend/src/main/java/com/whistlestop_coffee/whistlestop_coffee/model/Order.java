package com.whistlestop_coffee.whistlestop_coffee.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private String pickupTime;
    private String status;
    private String cancelReason;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    public Order() {}

    public Order(int id, Customer customer, String pickupTime) {
        this.id = id;
        this.customer = customer;
        this.pickupTime = pickupTime;
        this.status = "Pending";
        this.items = new ArrayList<>();
    }

    public int getId() { return id; }
    public Customer getCustomer() { return customer; }
    public String getPickupTime() { return pickupTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String reason) { this.cancelReason = reason; }
    public List<OrderItem> getItems() { return items; }

    public void addItem(OrderItem item) {
        item.setOrder(this);
        this.items.add(item);
    }

    public double getTotalPrice() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getSubtotal().doubleValue();
        }
        return total;
    }

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