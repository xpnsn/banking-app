package com.safevault.security.controller;

import com.safevault.security.service.impl.AdminServiceImpl;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/security/admin")
public class AdminController {

    AdminServiceImpl adminService;

    public AdminController(AdminServiceImpl adminService) {
        this.adminService = adminService;
    }

    @Hidden
    @PostMapping("/test")
    public String adminTest() {
        return "ADMIN";
    }

    @Operation(summary = "Add Admin", description = "Adds new admin!")
    @PostMapping("/add")
    public ResponseEntity<?> addAdmin(@RequestParam String username) {
        return adminService.addAdmin(username);
    }

    @Operation(summary = "Remove Admin", description = "Removes new admin!")
    @PostMapping("/remove")
    public ResponseEntity<?> removeAdmin(@RequestParam String username) {
        return adminService.removeAdmin(username);
    }

}
