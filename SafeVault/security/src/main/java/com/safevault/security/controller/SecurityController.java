package com.safevault.security.controller;

import com.safevault.security.dto.LoginRequest;
import com.safevault.security.dto.RegistrationRequest;
import com.safevault.security.feignClient.NotificationClient;
import com.safevault.security.service.AuthService;
import com.safevault.security.service.RedisService;
import com.safevault.security.service.impl.RedisServiceImpl;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Login", description = "This will generate a auth token!")

    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest user) {
        return authService.generateToken(user);
    }

    @PostMapping("/register")
    @Operation(summary = "Sign-Up", description = "Use to add new user into database")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest user) {
        return authService.saveUser(user);
    }

    @Hidden
    @PostMapping("/validate")
    public ResponseEntity<?> validate() {
        return authService.getUser();
    }

    @Hidden
    @PostMapping("/add-account-to-user")
    public ResponseEntity<?> addAccountToUser(@RequestParam String accountId) {
        return authService.addAccountToUser(accountId);
    }

    @Hidden
    @PostMapping("/remove-account-from-user")
    public ResponseEntity<?> removeAccountFromUser(@RequestParam String accountId, @RequestParam String password) {
        return authService.removeAccountFromUser(accountId, password);
    }

    @Hidden
    @GetMapping("/get-phone-number/{userId}")
    public ResponseEntity<?> getPhoneNumber(@PathVariable String userId) {
        return authService.getPhoneNumber(userId);
    }

    @Operation(summary = "Generate OTP", description = "phone number verification")
    @GetMapping("/generate-otp/phone")
    public ResponseEntity<?> generateSmsOTP() {
        return redisService.generateSmsOTP();
    }

    @Operation(summary = "Submit OTP", description = "phone number verification")
    @PostMapping("/validate-otp/phone/{otp}")
    public ResponseEntity<?> validateSmsOTP(@PathVariable String otp) {
        return redisService.validateSmsOtp(otp);
    }

    @Operation(summary = "Generate OTP", description = "email verification")
    @GetMapping("/generate-otp/email")
    public ResponseEntity<?> generateEmailOTP() {
        return redisService.generateEmailOTP();
    }

    @Hidden
    @PostMapping("kafka")
    public ResponseEntity<?> kafka(@RequestParam String message) {return authService.kafkaTest(message);}

    @Operation(summary = "Submit OTP", description = "email verification")
    @PostMapping("/validate-otp/email/{otp}")
    public ResponseEntity<?> validateEmailOTP(@PathVariable String otp) {
        return redisService.validateEmailOtp(otp);
    }
}
