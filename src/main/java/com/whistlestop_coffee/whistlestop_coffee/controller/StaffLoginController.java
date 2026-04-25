package com.whistlestop_coffee.whistlestop_coffee.controller;

import com.whistlestop_coffee.whistlestop_coffee.dto.LoginRequest;
import com.whistlestop_coffee.whistlestop_coffee.model.Staff;
import com.whistlestop_coffee.whistlestop_coffee.service.LoginManager;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.List;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin
public class StaffLoginController {

    private LoginManager loginManager = new LoginManager();

    private List<Staff> staffList = new ArrayList<>();

    // 构造函数（初始化数据）
    public StaffLoginController() {
        staffList.add(new Staff(1, "Admin", "admin@email.com", "123456"));
        staffList.add(new Staff(2, "Staff", "staff@email.com", "password"));
    }

    @PostMapping("/login")
    public Staff login(@RequestBody LoginRequest request) {
        return loginManager.login(
                request.getEmail(),
                request.getPassword(),
                staffList
        );
    }
}
