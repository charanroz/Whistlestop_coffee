package com.whistlestop_coffee.whistlestop_coffee.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    private String size;

    @Transient
    private int menuItemId;

    private int quantity;

    private BigDecimal unitPrice;

    public OrderItem() {}

    // ✅ FIXED CONSTRUCTOR
    public OrderItem(MenuItem menuItem, String size, int quantity, BigDecimal unitPrice) {
        this.menuItem = menuItem;
        this.size = (size != null) ? size : "Regular";
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public int getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    public MenuItem getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getItemName() {
        return (menuItem != null) ? menuItem.getName() : "Unknown";
    }

    public double getPrice() {
        return unitPrice != null ? unitPrice.doubleValue() : 0;
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(new BigDecimal(quantity));
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "menuItem=" + (menuItem != null ? menuItem.getName() : "null") +
                ", size='" + size + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=£" + unitPrice +
                ", subtotal=£" + getSubtotal() +
                '}';
    }
}