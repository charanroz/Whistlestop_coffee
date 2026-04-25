package com.whistlestop_coffee.whistlestop_coffee.repository;

import com.whistlestop_coffee.whistlestop_coffee.model.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerRepository {

    private List<Customer> customers;

    public CustomerRepository() {
        customers = new ArrayList<>();

        // 模拟数据库里的用户
        customers.add(new Customer("Alice", "alice@email.com", "123456"));
        customers.add(new Customer("Bob", "bob@email.com", "password"));
    }

    public Customer findByEmail(String email) {

        for (Customer c : customers) {
            if (c.getEmail().equals(email)) {
                return c;
            }
        }

        return null;
    }
}

