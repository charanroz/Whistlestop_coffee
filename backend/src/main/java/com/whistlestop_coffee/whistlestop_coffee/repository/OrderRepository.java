package com.whistlestop_coffee.whistlestop_coffee.repository;

import com.whistlestop_coffee.whistlestop_coffee.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByCustomerId(int customerId);

    List<Order> findByArchivedFalse();

    List<Order> findByArchivedTrue();

    List<Order> findByStatusIn(List<String> statuses);
}