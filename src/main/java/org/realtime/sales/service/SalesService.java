package org.realtime.sales.service;


import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
public class SalesService {


    private final RedisTemplate<String, Object> redisTemplate;

    public SalesService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void updateSales(String terminalId, String paymentMethod, int amount) {

        String dateKey = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String key = "dailySales:" + dateKey + ":terminal:" + terminalId + ":method:" + paymentMethod;


        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        long secondsUntilMidnight = ChronoUnit.SECONDS.between(now, midnight);


        redisTemplate.execute((RedisCallback<Object>) (connection) -> {
            byte[] redisKey = redisTemplate.getStringSerializer().serialize(key);

            connection.incrBy(redisKey, amount);
            connection.expire(redisKey, secondsUntilMidnight);

            return null;
        });
    }
}
