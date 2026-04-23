package com.edumanage.notificationservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class DeduplicationService {

    private static final Duration TTL = Duration.ofHours(24);
    private final StringRedisTemplate redisTemplate;

    /**
     * Returns true if this eventId has NOT been processed before (first time).
     * Marks it as processed atomically via SETNX.
     */
    public boolean isFirstOccurrence(String eventId) {
        Boolean set = redisTemplate.opsForValue().setIfAbsent("notif:" + eventId, "1", TTL);
        return Boolean.TRUE.equals(set);
    }
}
