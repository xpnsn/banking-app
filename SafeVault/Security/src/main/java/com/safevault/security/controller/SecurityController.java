package com.safevault.security.controller;

import com.safevault.security.entity.UserEntity;
import com.safevault.security.service.AuthService;
import com.safevault.security.service.CustomUserDetailsService;
import com.safevault.security.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/security")
public class SecurityController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserEntity user) {
        return authService.generateToken(user);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserEntity user) {
        return authService.saveUser(user);
    }

    @PostMapping("/validate")
    public String validate() {
        return "VALID";
    }
}
