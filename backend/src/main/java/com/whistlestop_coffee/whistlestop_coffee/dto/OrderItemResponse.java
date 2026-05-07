package com.whistlestop_coffee.whistlestop_coffee.dto;

import com.whistlestop_coffee.whistlestop_coffee.model.OrderItem;

/**
 * Simplifies order item details, extracting only the necessary display name and quantity.
 */
public class OrderItemResponse {
    public String itemName;
    public String size;
    public int quantity;

    public static OrderItemResponse from(OrderItem item) {
        OrderItemResponse res = new OrderItemResponse();

        // Safe check in case the linked MenuItem has been modified or removed
        res.itemName = (item.getMenuItem() != null) ? item.getMenuItem().getName() : "Unknown Item";
        res.size = item.getSize();
        res.quantity = item.getQuantity();
        return res;
    }
}