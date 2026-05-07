package com.whistlestop_coffee.whistlestop_coffee.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Join entity resolving the many-to-many relationship between Orders and MenuItems.
 */
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int quantity;
    private String size;

    /**
     * Price snapshot at the time of purchase.
     * Ensures historical order totals don't change if the global menu price is updated later.
     */
    private BigDecimal unitPrice;

    /**
     * Temporary field used for mapping incoming JSON payloads before the JPA association is built.
     * Not persisted to the database.
     */
    @Transient
    private Integer menuItemId;

    @ManyToOne
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;


    public int getId() { return id; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public MenuItem getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Integer getMenuItemId() { return menuItemId; }
    public void setMenuItemId(Integer menuItemId) { this.menuItemId = menuItemId; }
}