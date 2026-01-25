package com.safevault.security.service.impl;

import com.safevault.security.dto.ApiMessageResponse;
import com.safevault.security.entity.UserEntity;
import com.safevault.security.feignClient.NotificationClient;
import com.safevault.security.repository.RedisRepository;
import com.safevault.security.repository.UserRepository;
import com.safevault.security.service.KafkaProducer;
import com.safevault.security.service.RedisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;

@Service
public class RedisServiceImpl implements RedisService {

    private final RedisRepository redisRepository;
    private final NotificationClient notificationClient;
    private final UserRepository userRepository;
    private final KafkaProducer kafkaProducer;

    public RedisServiceImpl(
            RedisRepository redisRepository, NotificationClient notificationClient,
            UserRepository userRepository,
            KafkaProducer kafkaProducer) {
        this.redisRepository = redisRepository;
        this.notificationClient = notificationClient;
        this.userRepository = userRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public ResponseEntity<?> generateSmsOTP() {
        String name = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        UserEntity userEntity = userRepository.findByUsername(name);
        if(userEntity.getVerifiedPhoneNumber()) {
            return ResponseEntity.badRequest().body(ApiMessageResponse.of("PHONE NUMBER ALREADY VERIFIED!", HttpStatus.BAD_REQUEST));
        }
        String userId = String.valueOf(userEntity.getId());
        if(!redisRepository.validForOpt(userId, "SMS")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiMessageResponse.of("TIMEOUT OF "+redisRepository.getTimeoutTime(userId, "SMS"), HttpStatus.BAD_REQUEST));
        }
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            otp.append(random.nextInt(10));
        }
//        notificationClient.sendMessage(userEntity.getPhoneNumber(), otp.toString());
        kafkaProducer.send(userEntity.getPhoneNumber(), "SMS", Map.of("otp", otp.toString(), "type", "otp"), 2);
        redisRepository.saveOtp(userId, otp.toString(), "SMS");
        return ResponseEntity.ok(ApiMessageResponse.of("OTP SENT!", HttpStatus.OK));
    }

    @Override
    public ResponseEntity<?> validateSmsOtp(String otp) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByUsername(name);
        String userId = String.valueOf(userEntity.getId());
        if(redisRepository.getOtp(userId, "SMS") == null) {
            return ResponseEntity.badRequest().body(ApiMessageResponse.of("GENERATE NEW OTP!", HttpStatus.BAD_REQUEST));
        }
        if(otp.equals(redisRepository.getOtp(userId, "SMS"))) {
            userEntity.setVerifiedPhoneNumber(true);
            userRepository.save(userEntity);
            redisRepository.clear(userId, "SMS");
            return ResponseEntity.ok(ApiMessageResponse.of("OTP VERIFIED!", HttpStatus.OK));
        } else {
            int l = redisRepository.retryCount(userId, "SMS");
            if(l > 5) {
                redisRepository.addCooldown(userId, 3600, "SMS");
                redisRepository.resetRetryCount(userId, "SMS");
            }
            return ResponseEntity.badRequest().body(ApiMessageResponse.of("INVALID OTP!", HttpStatus.BAD_REQUEST));
        }
    }

    @Override
    public ResponseEntity<?> generateEmailOTP() {
        String name = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        UserEntity userEntity = userRepository.findByUsername(name);
        if(userEntity.getVerifiedEmailAddress()) {
            return ResponseEntity.badRequest().body(ApiMessageResponse.of("EMAIL ALREADY VERIFIED!", HttpStatus.BAD_REQUEST));
        }
        String userId = String.valueOf(userEntity.getId());
        if(!redisRepository.validForOpt(userId, "EMAIL")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiMessageResponse.of("TIMEOUT OF "+redisRepository.getTimeoutTime(userId, "EMAIL"), HttpStatus.BAD_REQUEST));
        }
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            otp.append(random.nextInt(10));
        }
//        notificationClient.sendMessage(userEntity.getPhoneNumber(), otp.toString());
        kafkaProducer.send(userEntity.getEmail(), "EMAIL", Map.of("otp", otp.toString(), "type", "otp"), 2);
        redisRepository.saveOtp(userId, otp.toString(), "EMAIL");
        return ResponseEntity.ok(ApiMessageResponse.of("OTP SENT!", HttpStatus.OK));
    }

    @Override
    public ResponseEntity<?> validateEmailOtp(String otp) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByUsername(name);
        String userId = String.valueOf(userEntity.getId());
        if(redisRepository.getOtp(userId, "EMAIL") == null) {
            return ResponseEntity.badRequest().body(ApiMessageResponse.of("GENERATE NEW OTP!", HttpStatus.BAD_REQUEST));
        }
        if(otp.equals(redisRepository.getOtp(userId, "EMAIL"))) {
            userEntity.setVerifiedEmailAddress(true);
            userRepository.save(userEntity);
            redisRepository.clear(userId, "EMAIL");
            return ResponseEntity.ok(ApiMessageResponse.of("OTP VERIFIED!", HttpStatus.OK));
        } else {
            int l = redisRepository.retryCount(userId, "EMAIL");
            if(l > 5) {
                redisRepository.addCooldown(userId, 3600, "EMAIL");
                redisRepository.resetRetryCount(userId, "EMAIL");
            }
            return ResponseEntity.badRequest().body(ApiMessageResponse.of("INVALID OTP!", HttpStatus.BAD_REQUEST));
        }
    }

    @Override
    public ResponseEntity<?> test(String userId, String body) {
        return notificationClient.sendMessage(userId, body);
    }
}
