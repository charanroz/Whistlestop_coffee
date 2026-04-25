package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.Customer;

public class LoginResult {

    private boolean success;
    private String message;
    private Customer customer;

    public LoginResult(boolean success, String message, Customer customer) {
        this.success = success;
        this.message = message;
        this.customer = customer;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Customer getCustomer() {
        return customer;
    }
}
