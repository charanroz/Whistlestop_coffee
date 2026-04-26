package com.whistlestop_coffee.whistlestop_coffee.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    private String size;
    private int quantity;
    private BigDecimal unitPrice;

    public OrderItem() {}

    public OrderItem(int menuItemId, String size, int quantity, BigDecimal unitPrice) {
        this.size = (size != null) ? size : "Regular";
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public MenuItem getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

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
                ", size='" + size + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=£" + unitPrice +
                ", subtotal=£" + getSubtotal() +
                '}';
    }
}