package com.safevault.security.controller;

import com.safevault.security.dto.LoginRequest;
import com.safevault.security.dto.RegistrationRequest;
import com.safevault.security.feignClient.NotificationClient;
import com.safevault.security.service.AuthService;
import com.safevault.security.service.RedisService;
import com.safevault.security.service.impl.RedisServiceImpl;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest user) {
        return authService.generateToken(user);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest user) {
        return authService.saveUser(user);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate() {
        return authService.getUser();
    }

    @PostMapping("/add-account-to-user")
    public ResponseEntity<?> addAccountToUser(@RequestParam String accountId) {
        return authService.addAccountToUser(accountId);
    }

    @PostMapping("/remove-account-from-user")
    public ResponseEntity<?> removeAccountFromUser(@RequestParam String accountId, @RequestParam String password) {
        return authService.removeAccountFromUser(accountId, password);
    }

    @GetMapping("/get-phone-number/{userId}")
    public ResponseEntity<?> getPhoneNumber(@PathVariable String userId) {
        return authService.getPhoneNumber(userId);
    }

    @GetMapping("/generate-otp/phone")
    public ResponseEntity<?> generateSmsOTP() {
        return redisService.generateSmsOTP();
    }

    @PostMapping("/validate-otp/phone/{otp}")
    public ResponseEntity<?> validateSmsOTP(@PathVariable String otp) {
        return redisService.validateSmsOtp(otp);
    }

    @GetMapping("/generate-otp/email")
    public ResponseEntity<?> generateEmailOTP() {
        return redisService.generateEmailOTP();
    }

    @PostMapping("/validate-otp/email/{otp}")
    public ResponseEntity<?> validateEmailOTP(@PathVariable String otp) {
        return redisService.validateEmailOtp(otp);
    }

    @PostMapping("kafka")
    public ResponseEntity<?> kafka(@RequestParam String message) {return authService.kafkaTest(message);}
}
