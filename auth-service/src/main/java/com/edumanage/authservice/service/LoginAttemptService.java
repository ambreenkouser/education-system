package com.edumanage.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAttemptService {

    private static final String KEY_PREFIX = "login:fail:";

    @Value("${login.max-attempts:5}")
    private int maxAttempts;

    @Value("${login.lock-duration-minutes:15}")
    private int lockDurationMinutes;

    private final StringRedisTemplate redisTemplate;

    public void recordFailure(String email) {
        String key = KEY_PREFIX + email.toLowerCase();
        Long attempts = redisTemplate.opsForValue().increment(key);
        // Set TTL only on first failure so the window resets naturally
        if (attempts != null && attempts == 1L) {
            redisTemplate.expire(key, Duration.ofMinutes(lockDurationMinutes));
        }
        log.warn("Failed login attempt #{} for email={}", attempts, email);
    }

    public void clearAttempts(String email) {
        redisTemplate.delete(KEY_PREFIX + email.toLowerCase());
    }

    public boolean isBlocked(String email) {
        String val = redisTemplate.opsForValue().get(KEY_PREFIX + email.toLowerCase());
        if (val == null) return false;
        try {
            return Integer.parseInt(val) >= maxAttempts;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
