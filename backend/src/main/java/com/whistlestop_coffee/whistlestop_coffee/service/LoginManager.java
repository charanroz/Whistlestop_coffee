package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.Staff;
import java.util.List;

public class LoginManager {
    public Staff login(String email, String password, List<Staff> staffList){
        for(Staff staff : staffList){
            if(staff.getEmail().equals(email) && staff.getPassword().equals(password)){
                return staff;
            }
        }
        return null;
    }
}
