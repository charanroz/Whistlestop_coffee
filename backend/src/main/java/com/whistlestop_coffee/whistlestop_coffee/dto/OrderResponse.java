package com.whistlestop_coffee.whistlestop_coffee.dto;

import com.whistlestop_coffee.whistlestop_coffee.model.Order;
import java.util.List;
import java.util.stream.Collectors;

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
        res.customerName = (order.getCustomer() != null) ? order.getCustomer().getName() : "Guest";
        res.pickupTime = order.getPickupTime();
        res.status = order.getStatus();
        res.totalPrice = order.getTotalPrice();

        // 将 Order 实体中的 OrderItem 列表转换为 DTO 列表
        res.items = order.getItems().stream()
                .map(OrderItemResponse::from)
                .collect(Collectors.toList());

        //OrderItem to DTO
        if (order.getItems() != null) {
            res.items = order.getItems().stream()
                    .map(OrderItemResponse::from)
                    .collect(Collectors.toList());
        }
        return res;
    }
}