package com.task.ecommerce.service;

import com.task.ecommerce.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:";

    public void blacklist(String token) {

        UUID jti = jwtUtil.extractJti(token);
        Date expiry = jwtUtil.extractExpiration(token);

        long ttl =
                expiry.getTime() - System.currentTimeMillis();

        if (ttl <= 0) {
            return;
        }

        redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + jti,
                "true",
                Duration.ofMillis(ttl)
        );
    }

    public boolean isBlacklisted(UUID jti) {

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(
                        BLACKLIST_PREFIX + jti
                )
        );
    }
}
