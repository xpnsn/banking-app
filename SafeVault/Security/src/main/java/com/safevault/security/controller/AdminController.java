package com.safevault.security.controller;

import com.safevault.security.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/security/admin")
public class AdminController {

    @Autowired
    AdminServiceImpl adminService;

    @PostMapping("/test")
    public String adminTest() {
        return "ADMIN";
    }

    @PostMapping("/add")
    public ResponseEntity<?> addAdmin(@RequestParam String username) {
        return adminService.addAdmin(username);
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeAdmin(@RequestParam String username) {
        return adminService.removeAdmin(username);
    }

}
