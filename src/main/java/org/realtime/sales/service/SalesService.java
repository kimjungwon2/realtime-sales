package org.realtime.sales.service;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class SalesService {


    private final RedisTemplate<String, Object> redisTemplate;

    public SalesService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void updateSales(String terminalId, String paymentMethod, int amount) {
        String key = "sales:" + terminalId;

        redisTemplate.opsForHash().increment(key, paymentMethod, amount);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        long secondsUntilMidnight = ChronoUnit.SECONDS.between(now, midnight);

        redisTemplate.expire(key, Duration.ofSeconds(secondsUntilMidnight));
    }
}
