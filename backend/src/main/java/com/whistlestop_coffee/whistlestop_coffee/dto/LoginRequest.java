package com.whistlestop_coffee.whistlestop_coffee.dto;

/**
 * Simple POJO for capturing login credentials from the request body.
 */
public class LoginRequest {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
