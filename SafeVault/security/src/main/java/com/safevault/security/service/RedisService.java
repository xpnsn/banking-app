package com.safevault.security.service;

import org.springframework.http.ResponseEntity;

public interface RedisService {

    ResponseEntity<?> generateOTP();
    ResponseEntity<?> validateOtp(String otp);

    ResponseEntity<?> test(String userId, String body);
}
