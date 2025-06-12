package com.safevault.security.service.impl;

import com.safevault.security.entity.UserEntity;
import com.safevault.security.feignClient.NotificationClient;
import com.safevault.security.repository.RedisRepository;
import com.safevault.security.repository.UserRepository;
import com.safevault.security.service.RedisService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Objects;

@Service
public class RedisServiceImpl implements RedisService {

    private final RedisRepository redisRepository;
    private final NotificationClient notificationClient;
    private final UserRepository userRepository;
    public RedisServiceImpl(
            RedisRepository redisRepository, NotificationClient notificationClient,
            UserRepository userRepository
    ) {
        this.redisRepository = redisRepository;
        this.notificationClient = notificationClient;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<?> generateOTP() {
        String name = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        UserEntity userEntity = userRepository.findByUsername(name);
        if(userEntity.getVerifiedPhoneNumber()) {
            return ResponseEntity.badRequest().body("PHONE NUMBER ALREADY VERIFIED!");
        }
        String userId = String.valueOf(userEntity.getId());
        if(!redisRepository.validForOpt(userId)) {
            return ResponseEntity.ok("TIMEOUT OF "+redisRepository.getTimeoutTime(userId));
        }
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            otp.append(random.nextInt(10));
        }
        String message = "DON'T SHARE. "+otp+" is your one time password for SafeVault.";
        notificationClient.sendMessage(userEntity.getPhoneNumber(), otp.toString());
        redisRepository.saveOtp(userId, otp.toString());
        return ResponseEntity.ok("OTP SENT!");
    }

    @Override
    public ResponseEntity<?> validateOtp(String otp) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByUsername(name);
        String userId = String.valueOf(userEntity.getId());
        if(redisRepository.getOtp(userId) == null) {
            return ResponseEntity.badRequest().body("GENERATE NEW OTP!");
        }
        if(otp.equals(redisRepository.getOtp(userId))) {
            userEntity.setVerifiedPhoneNumber(true);
            userRepository.save(userEntity);
            redisRepository.clear(userId);
            return ResponseEntity.ok("OTP VERIFIED!");
        } else {
            int l = redisRepository.retryCount(userId);
            if(l > 5) {
                redisRepository.addCooldown(userId, 3600);
                redisRepository.resetRetryCount(userId);
            }
            return ResponseEntity.badRequest().body("INVALID OTP!");
        }
    }

    @Override
    public ResponseEntity<?> test(String userId, String body) {
        return notificationClient.sendMessage(userId, body);
    }
}
