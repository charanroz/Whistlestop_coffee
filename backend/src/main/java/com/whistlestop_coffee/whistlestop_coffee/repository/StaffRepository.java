package com.whistlestop_coffee.whistlestop_coffee.repository;

import com.whistlestop_coffee.whistlestop_coffee.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Integer> {
    Optional<Staff> findByEmail(String email);
}