package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.Customer;
import com.whistlestop_coffee.whistlestop_coffee.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.whistlestop_coffee.whistlestop_coffee.dto.LoginResult;

@Service
public class CustomerLoginService {

    @Autowired
    private CustomerRepository repository;

    //  LOGIN
    public LoginResult login(String email, String password) {

        if (email == null || email.trim().isEmpty()) {
            return new LoginResult(false, "Email cannot be empty", null);
        }

        if (password == null || password.trim().isEmpty()) {
            return new LoginResult(false, "Password cannot be empty", null);
        }

        Customer customer = repository.findByEmail(email.trim()).orElse(null);

        if (customer == null) {
            return new LoginResult(false, "Customer not found", null);
        }

        if (!customer.getPassword().equals(password)) {
            return new LoginResult(false, "Incorrect password", null);
        }

        return new LoginResult(true, "Login successful", customer);
    }

    // SIGNUP CREATE NEW USER
    public LoginResult signup(String name, String email, String password) {

        if (name == null || name.trim().isEmpty()) {
            return new LoginResult(false, "Name cannot be empty", null);
        }

        if (email == null || email.trim().isEmpty()) {
            return new LoginResult(false, "Email cannot be empty", null);
        }

        if (password == null || password.trim().isEmpty()) {
            return new LoginResult(false, "Password cannot be empty", null);
        }

        // check if already exists
        if (repository.findByEmail(email.trim()).isPresent()) {
            return new LoginResult(false, "Email already exists", null);
        }

        Customer customer = new Customer(name, email, password);
        repository.save(customer);

        return new LoginResult(true, "Signup successful", customer);
    }

    public boolean existsByEmail(String email) {
        return repository.findByEmail(email).isPresent();
    }

    public Customer register(Customer customer) {
        return repository.save(customer);
    }
}