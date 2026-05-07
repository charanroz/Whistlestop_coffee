package com.whistlestop_coffee.whistlestop_coffee.dto;

/**
 * Standardized response for all authentication attempts.
 * Includes a status flag and descriptive message for frontend toast notifications.
 */
public class LoginResult {

    private boolean success;
    private String message;
    private Object customer;


    public LoginResult(boolean success, String message, Object customer) {
        this.success = success;
        this.message = message;
        this.customer = customer;
    }

    public boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getCustomer() {
        return customer;
    }
}