package com.whistlestop_coffee.whistlestop_coffee.repository;

import com.whistlestop_coffee.whistlestop_coffee.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Spring Data JPA handles the basic SQL queries automatically.
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {

    // Custom query to get the full menu, but hiding the items we "deleted".
    List<MenuItem> findByIsDeletedFalse();

    // Custom query for customers: only show items that are NOT deleted AND in stock.
    List<MenuItem> findByIsAvailableTrueAndIsDeletedFalse();
}