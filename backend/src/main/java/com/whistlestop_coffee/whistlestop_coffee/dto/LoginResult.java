package com.whistlestop_coffee.whistlestop_coffee.dto;

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