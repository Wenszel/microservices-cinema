package com.example.token;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class TokenCache {
    private final RedisTemplate<String, String> template;
    private final String TOKEN_KEY = "payment_access_token";

    public TokenCache(RedisTemplate<String, String> template) {
        this.template = template;
    }

    public void addToken(String token, int expirationTime) {
        template.opsForValue().set(TOKEN_KEY, token);
        Duration expiration = Duration.ofSeconds(expirationTime);
        template.expire(TOKEN_KEY, expiration);
    }

    public String getToken() {
        return template.opsForValue().get(TOKEN_KEY);
    }
}
