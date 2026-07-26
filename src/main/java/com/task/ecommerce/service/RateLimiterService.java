package com.task.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "login:";


    public boolean isAllowed(String ip, int maxAttempts, int windowMinutes) {

        String key = PREFIX + ip;

        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        String value = ops.get(key);

        Integer attempts = value == null ? null : Integer.valueOf(value);

        if (attempts == null) {

            ops.set(
                    key,
                    String.valueOf(1),
                    Duration.ofMinutes(windowMinutes)
            );

            return true;
        }

        if (attempts >= maxAttempts) {
            return false;
        }

        ops.increment(key);

        return true;
    }

    public void reset(String ip) {
        redisTemplate.delete(PREFIX + ip);
    }
}
