package com.whistlestop_coffee.whistlestop_coffee.dto;

import com.whistlestop_coffee.whistlestop_coffee.model.OrderItem;

public class OrderItemResponse {
    public String itemName;
    public String size;
    public int quantity;

    public static OrderItemResponse from(OrderItem item) {
        OrderItemResponse res = new OrderItemResponse();

        res.itemName = (item.getMenuItem() != null) ? item.getMenuItem().getName() : "Unknown Item";
        res.size = item.getSize();
        res.quantity = item.getQuantity();
        return res;
    }
}