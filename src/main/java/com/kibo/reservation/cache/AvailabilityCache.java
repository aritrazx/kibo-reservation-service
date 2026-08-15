package com.kibo.reservation.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AvailabilityCache {
    private final StringRedisTemplate redis;
    private static final Duration TTL = Duration.ofSeconds(30);

    public Optional<Integer> get(Long dropId) {
        try {
            String value = redis.opsForValue().get(key(dropId));
            if (value == null) {
                return Optional.empty();
            }
            return Optional.of(Integer.parseInt(value));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void put(Long dropId, int available) {
        try {
            redis.opsForValue().set(key(dropId), String.valueOf(available), TTL);
        } catch (Exception ignored) {
            // Redis is optional for correctness; fail open and let the database remain the source of truth.
        }
    }

    public void evict(Long dropId) {
        try {
            redis.delete(key(dropId));
        } catch (Exception ignored) {
            // Ignore cache failures; the database remains authoritative.
        }
    }

    private String key(Long dropId) {
        return "drop:" + dropId + ":availability";
    }
}
