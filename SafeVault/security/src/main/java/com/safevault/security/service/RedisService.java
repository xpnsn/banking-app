package com.safevault.security.service;

import org.springframework.http.ResponseEntity;

public interface RedisService {

    ResponseEntity<?> generateSmsOTP();
    ResponseEntity<?> generateEmailOTP();
    ResponseEntity<?> validateSmsOtp(String otp);
    ResponseEntity<?> validateEmailOtp(String otp);

    ResponseEntity<?> test(String userId, String body);
}
