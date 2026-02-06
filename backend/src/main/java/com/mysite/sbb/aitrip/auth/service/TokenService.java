package com.mysite.sbb.aitrip.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private static final String RT_PREFIX = "RT:";
    private static final String BL_PREFIX = "BL:";
    private final RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(String email, String refreshToken, long expirationMs) {
        redisTemplate.opsForValue().set(RT_PREFIX + email, refreshToken, expirationMs, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(String email) {
        return redisTemplate.opsForValue().get(RT_PREFIX + email);
    }

    public void deleteRefreshToken(String email) {
        redisTemplate.delete(RT_PREFIX + email);
    }

    public void addToBlacklist(String accessToken, long remainingExpirationMs) {
        if (remainingExpirationMs > 0) {
            redisTemplate.opsForValue().set(BL_PREFIX + accessToken, "logout", remainingExpirationMs, TimeUnit.MILLISECONDS);
        }
    }

    public boolean isBlacklisted(String accessToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BL_PREFIX + accessToken));
    }
}
