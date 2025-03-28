package com.safevault.security.controller;

import com.safevault.security.dto.LoginRequest;
import com.safevault.security.dto.RegistrationRequest;
import com.safevault.security.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/security")
public class SecurityController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest user) {
        return authService.generateToken(user);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest user) {
        return authService.saveUser(user);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate() {
        return authService.getUser();
    }

    @PostMapping("/add-account-to-user")
    public ResponseEntity<String> addAccountToUser(@RequestParam String accountId) {
        return authService.addAccountToUser(accountId);
    }
}
