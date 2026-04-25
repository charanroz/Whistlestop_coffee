
package com.whistlestop_coffee.whistlestop_coffee.service;


import com.whistlestop_coffee.whistlestop_coffee.model.Customer;
import com.whistlestop_coffee.whistlestop_coffee.repository.CustomerRepository;

import model.Customer;
import repository.CustomerRepository;

public class CustomerLoginService {

    private CustomerRepository repository;

    public CustomerLoginService() {
        repository = new CustomerRepository();
    }

    public LoginResult login(String email, String password) {

        if (email == null || email.trim().isEmpty()) {
            return new LoginResult(false, "Email cannot be empty", null);
        }

        if (password == null || password.trim().isEmpty()) {
            return new LoginResult(false, "Password cannot be empty", null);
        }

        Customer customer = repository.findByEmail(email.trim());

        if (customer == null) {
            return new LoginResult(false, "Customer not found", null);
        }

        if (!customer.getPassword().equals(password)) {
            return new LoginResult(false, "Incorrect password", null);
        }

        return new LoginResult(true, "Login successful", customer);
    }
}

