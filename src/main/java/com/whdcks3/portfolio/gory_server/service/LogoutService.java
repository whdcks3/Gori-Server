package com.whdcks3.portfolio.gory_server.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LogoutService {
    private final RedisTemplate<String, String> redisTemplate;

    public void addToBlacklist(String token, Duration expiration) {
        redisTemplate.opsForValue().set(token, "blacklisted", expiration);
    }

    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }
}
