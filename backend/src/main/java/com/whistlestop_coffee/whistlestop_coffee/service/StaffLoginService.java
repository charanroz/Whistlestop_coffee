package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.Staff;
import com.whistlestop_coffee.whistlestop_coffee.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.whistlestop_coffee.whistlestop_coffee.dto.LoginResult;

@Service
public class StaffLoginService {

    @Autowired
    private StaffRepository repository;

    public LoginResult login(String email, String password) {

        Staff staff = repository.findByEmail(email).orElse(null);

        if (staff == null) {
            return new LoginResult(false, "Staff not found", null);
        }

        if (!staff.getPassword().equals(password)) {
            return new LoginResult(false, "Incorrect password", null);
        }

        return new LoginResult(true, "Login successful", staff);
    }
}