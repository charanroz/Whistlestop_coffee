package com.whistlestop_coffee.whistlestop_coffee.dto;

import com.whistlestop_coffee.whistlestop_coffee.model.Order;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Formats order data for the UI.
 * Replaces complex object relationships with simple fields for easier frontend parsing.
 */
public class OrderResponse {

    public int orderId;
    public String customerName;
    public String pickupTime;
    public String status;
    public double totalPrice;
    public List<OrderItemResponse> items;
    public String cancellationReason;

    public static OrderResponse from(Order order) {

        OrderResponse res = new OrderResponse();

        res.orderId = order.getId();

        // Handling potential nulls for Guest checkouts
        res.customerName = (order.getCustomer() != null)
                ? order.getCustomer().getName()
                : "Guest";

        res.pickupTime = order.getPickupTime();
        res.status = order.getStatus();

        res.totalPrice = (order.getTotal() != null)
                ? order.getTotal().doubleValue()
                : 0.0;

        if (order.getItems() != null) {
            res.items = order.getItems().stream()
                    .map(OrderItemResponse::from)
                    .collect(Collectors.toList());
        }

        res.cancellationReason = order.getCancelReason();

        return res;
    }
}