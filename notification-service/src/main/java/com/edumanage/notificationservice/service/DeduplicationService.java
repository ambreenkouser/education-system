package com.edumanage.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeduplicationService {

    private static final Duration TTL = Duration.ofHours(24);
    private final StringRedisTemplate redisTemplate;

    /**
     * Returns true if this eventId has NOT been processed before (first time).
     * Falls back to allowing the event through if Redis is unavailable — better
     * to send a duplicate email than to silently drop a critical notification.
     */
    public boolean isFirstOccurrence(String eventId) {
        try {
            Boolean set = redisTemplate.opsForValue().setIfAbsent("notif:" + eventId, "1", TTL);
            return Boolean.TRUE.equals(set);
        } catch (Exception e) {
            log.warn("Redis unavailable for deduplication check on eventId={}. " +
                     "Allowing event through to prevent notification loss: {}", eventId, e.getMessage());
            return true;
        }
    }
}
