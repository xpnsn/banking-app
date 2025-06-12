package com.safevault.security.controller;

import com.safevault.security.dto.LoginRequest;
import com.safevault.security.dto.RegistrationRequest;
import com.safevault.security.feignClient.NotificationClient;
import com.safevault.security.service.AuthService;
import com.safevault.security.service.RedisService;
import com.safevault.security.service.impl.RedisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/security")
public class SecurityController {

    private final AuthService authService;
    private final RedisServiceImpl redisService;

    public SecurityController(AuthService authService, RedisServiceImpl redisService) {
        this.authService = authService;
        this.redisService = redisService;
    }

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
    public ResponseEntity<String> addAccountToUser(@RequestParam String accountId, @RequestParam String password) {
        return authService.addAccountToUser(accountId, password);
    }

    @PostMapping("/remove-account-from-user")
    public ResponseEntity<String> removeAccountFromUser(@RequestParam String accountId, @RequestParam String password) {
        return authService.removeAccountFromUser(accountId, password);
    }

    @GetMapping("/get-phone-number/{userId}")
    public ResponseEntity<?> getPhoneNumber(@PathVariable String userId) {
        return authService.getPhoneNumber(userId);
    }

    @GetMapping("/generate-otp")
    public ResponseEntity<?> generateOTP() {
        return redisService.generateOTP();
    }

    @PostMapping("/validate-otp/{otp}")
    public ResponseEntity<?> validateOTP(@PathVariable String otp) {
        return redisService.validateOtp(otp);
    }
}
