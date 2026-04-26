package com.whistlestop_coffee.whistlestop_coffee.repository;

import com.whistlestop_coffee.whistlestop_coffee.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {
    List<MenuItem> findByIsDeletedFalse();
    List<MenuItem> findByIsAvailableTrueAndIsDeletedFalse();
}