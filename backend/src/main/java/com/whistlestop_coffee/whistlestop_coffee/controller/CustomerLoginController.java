package com.whistlestop_coffee.whistlestop_coffee.controller;

import com.whistlestop_coffee.whistlestop_coffee.dto.LoginRequest;
import com.whistlestop_coffee.whistlestop_coffee.model.Customer;
import com.whistlestop_coffee.whistlestop_coffee.service.CustomerLoginService;
import com.whistlestop_coffee.whistlestop_coffee.dto.LoginResult;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin
public class CustomerLoginController {

    @Autowired
    private CustomerLoginService loginService;

    @PostMapping("/login")
    public LoginResult login(@RequestBody LoginRequest request) {
        return loginService.login(
                request.getEmail(),
                request.getPassword()
        );
    }

    @PostMapping("/signup")
    public LoginResult signup(@RequestBody Customer customer) {

        // check if email already exists
        if (loginService.existsByEmail(customer.getEmail())) {
            return new LoginResult(false, "Email already exists", null);
        }

        return loginService.signup(customer);
    }
}