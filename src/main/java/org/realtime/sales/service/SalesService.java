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

    public void updateSalesWithHincrby(String terminalId, String paymentMethod, int amount) {
        String dateKey = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String key = "dailySales:" + dateKey; // 키는 날짜 기준으로 설정

        // 해당 terminalId와 paymentMethod에 대한 필드 키 생성
        String field = "terminal:" + terminalId + ":method:" + paymentMethod;

        // 자정까지의 시간 계산
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        long secondsUntilMidnight = ChronoUnit.SECONDS.between(now, midnight);

        redisTemplate.execute((RedisCallback<Object>) (connection) -> {
            byte[] redisKey = redisTemplate.getStringSerializer().serialize(key);
            byte[] redisField = redisTemplate.getStringSerializer().serialize(field);

            // HINCRBY로 해당 필드의 매출 값을 증가시킴
            connection.hIncrBy(redisKey, redisField, amount);

            // TTL 설정 (자정까지)
            connection.expire(redisKey, secondsUntilMidnight);

            return null;
        });
    }

}
