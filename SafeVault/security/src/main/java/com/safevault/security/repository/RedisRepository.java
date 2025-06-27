package com.safevault.security.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Objects;

@Repository
public class RedisRepository {

    public RedisTemplate<String, Object> redisTemplate;

    public RedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveOtp(String userId, String otp, String type) {
        String otpKey = "OTP:" + type + userId;
        String retryKey = "RETRY:" + type + userId;
        redisTemplate.opsForValue().set(otpKey, otp, Duration.ofMinutes(5));
        addCooldown(userId, 120, type);
        redisTemplate.opsForValue().set(retryKey, "0", Duration.ofMinutes(10));
    }

    public int retryCount(String userId, String type) {
        String retryKey = "RETRY:" + type + userId;
        String temp = Objects.requireNonNull(redisTemplate.opsForValue().get(retryKey)).toString();
        int attempt = Integer.parseInt(temp)+1;
        redisTemplate.opsForValue().set(retryKey, attempt + "");
        return attempt;
    }

    public void resetRetryCount(String userId, String type) {
        redisTemplate.delete("RETRY:" + type + userId);
        redisTemplate.delete("OTP:" +type + userId);
    }

    public String getOtp(String userId, String type) {
        String key = "OTP:" + type + userId;
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void deleteOtp(String userId, String type) {
        String key = "OTP:" + type + userId;
        redisTemplate.delete(key);
    }
    public boolean validForOpt(String userId, String type) {
        String key = "COOLDOWN:" + type + userId;
        return !Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void clear(String userId, String type) {
        redisTemplate.delete("OTP:"+type+userId);
        redisTemplate.delete("COOLDOWN:"+type+userId);
        redisTemplate.delete("RETRY:"+type+userId);
    }

    public void addCooldown(String userId, long duration, String type) {
        String cooldownKey = "COOLDOWN:" + type + userId;
        redisTemplate.opsForValue().set(cooldownKey, "1", Duration.ofSeconds(duration));
    }

    public String getTimeoutTime(String userId, String type) {
        String key = "COOLDOWN:" + type + userId;
        if(Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            Long expire = redisTemplate.getExpire(key);
            Duration duration = Duration.ofSeconds(expire);
            int hours = duration.toHoursPart();
            int minutes = duration.toMinutesPart();
            int seconds = duration.toSecondsPart();
            StringBuilder time = new StringBuilder();

            if (hours > 0) time.append(hours).append("h ");
            if (minutes > 0) time.append(minutes).append("m ");
            if (seconds > 0) time.append(seconds).append("s");

            return time.toString().trim();
        } else {
            return "-1";
        }
    }
}
