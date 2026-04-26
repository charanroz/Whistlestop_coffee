package com.whistlestop_coffee.whistlestop_coffee.repository;

import com.whistlestop_coffee.whistlestop_coffee.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
}