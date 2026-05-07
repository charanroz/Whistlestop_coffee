package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.Staff;
import com.whistlestop_coffee.whistlestop_coffee.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.whistlestop_coffee.whistlestop_coffee.dto.LoginResult;

import java.util.List;

/**
 * Handles authentication for staff accessing the management dashboard.
 */
@Service
public class StaffLoginService {

    @Autowired
    private StaffRepository repository;

    public LoginResult login(String email, String password) {

        List<Staff> staffList = repository.findAllByEmail(email);

        if (staffList.isEmpty()) {
            return new LoginResult(false, "Staff not found", null);
        }

        Staff staff = staffList.get(0);

        // Note: Plaintext password comparison is used here for the university prototype.
        // In a real production system, this must be replaced with BCrypt hashing.
        if (!staff.getPassword().equals(password)) {
            return new LoginResult(false, "Incorrect password", null);
        }

        return new LoginResult(true, "Login successful", staff);
    }
}