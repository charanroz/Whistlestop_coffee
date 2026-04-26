package com.whistlestop_coffee.whistlestop_coffee;

import com.whistlestop_coffee.whistlestop_coffee.model.Customer;
import com.whistlestop_coffee.whistlestop_coffee.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(CustomerRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                Customer c = new Customer("Test User", "test@test.com", "1234");
                repo.save(c);
                System.out.println("✅ Default customer created (ID=1)");
            }
        };
    }
}