package com.whistlestop_coffee.whistlestop_coffee;

import com.whistlestop_coffee.whistlestop_coffee.model.Customer;
import com.whistlestop_coffee.whistlestop_coffee.model.Staff;
import com.whistlestop_coffee.whistlestop_coffee.repository.CustomerRepository;
import com.whistlestop_coffee.whistlestop_coffee.repository.StaffRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(CustomerRepository customerRepo, StaffRepository staffRepo) {
        return args -> {

            // Customer
            if (customerRepo.count() == 0) {
                Customer c = new Customer("Test User", "test@test.com", "1234");
                customerRepo.save(c);
            }

            // Staff
            if (staffRepo.findAllByEmail("admin@coffee.com").isEmpty()) {
                Staff admin = new Staff("Admin", "admin@coffee.com", "1234");
                staffRepo.save(admin);
            }
        };
    }
}