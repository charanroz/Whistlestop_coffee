package com.whistlestop_coffee.whistlestop_coffee.controller;

import com.whistlestop_coffee.whistlestop_coffee.dto.LoginRequest;
import com.whistlestop_coffee.whistlestop_coffee.service.CustomerLoginService;
import com.whistlestop_coffee.whistlestop_coffee.service.LoginResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling customer login requests.
 */
@RestController
@RequestMapping("/api/customer")
@CrossOrigin
public class CustomerLoginController {

    // Service used to process login logic
    @Autowired
    private CustomerLoginService loginService;

    /**
     * API endpoint for customer login.
     *
     * @param request contains email and password from the client
     * @return LoginResult indicating success or failure
     */
    @PostMapping("/login")
    public LoginResult login(@RequestBody LoginRequest request) {

        // Call service to validate login credentials
        return loginService.login(
                request.getEmail(),
                request.getPassword()
        );
    }
}
