package com.whistlestop_coffee.whistlestop_coffee.service;

public class LoginResult {

    private boolean success;
    private String message;
    private Object data; //

    public LoginResult(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}