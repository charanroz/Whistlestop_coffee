package com.whistlestop_coffee.whistlestop_coffee.repository;

import com.whistlestop_coffee.whistlestop_coffee.model.BusinessHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BusinessHourRepository extends JpaRepository<BusinessHour, String> {
    Optional<BusinessHour> findByDayOfWeekIgnoreCase(String dayOfWeek);
}