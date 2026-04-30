package com.whistlestop_coffee.whistlestop_coffee.controller;

import com.whistlestop_coffee.whistlestop_coffee.dto.LoginRequest;
import com.whistlestop_coffee.whistlestop_coffee.dto.LoginResult;
import com.whistlestop_coffee.whistlestop_coffee.service.StaffLoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin
public class StaffLoginController {

    @Autowired
    private StaffLoginService service;

    @PostMapping("/login")
    public LoginResult login(@RequestBody LoginRequest request) {
        return service.login(request.getEmail(), request.getPassword());
    }
}