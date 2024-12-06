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
        String key = "terminal:" + terminalId + ":date:" + dateKey + ":method:" + paymentMethod +":dailySales";

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        long secondsUntilMidnight = ChronoUnit.SECONDS.between(now, midnight);

        redisTemplate.execute((RedisCallback<Object>) (connection) -> {
            byte[] redisKey = redisTemplate.getStringSerializer().serialize(key);

            connection.incrBy(redisKey, amount);

            Long ttl = connection.ttl(redisKey);
            if (ttl == null || ttl == -1) {
                connection.expire(redisKey, secondsUntilMidnight);
            }

            return null;
        });
    }

    public void updateSalesWithHincrby(String terminalId, String paymentMethod, int amount) {
        String dateKey = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String key = "terminal:" + terminalId; // 키는 terminalId 기준
        String field = "date:" + dateKey + ":method:" + paymentMethod+":dailySales"; // 필드 키 생성

        // 자정까지의 시간 계산
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        long secondsUntilMidnight = ChronoUnit.SECONDS.between(now, midnight);

        redisTemplate.execute((RedisCallback<Object>) (connection) -> {
            byte[] redisKey = redisTemplate.getStringSerializer().serialize(key);
            byte[] redisField = redisTemplate.getStringSerializer().serialize(field);

            // HINCRBY로 해당 필드의 매출 값을 증가시킴
            connection.hIncrBy(redisKey, redisField, amount);

            // 키의 TTL 확인
            Long ttl = connection.ttl(redisKey);
            if (ttl == null || ttl == -1) { // -1이면 만료 설정이 없는 상태
                connection.expire(redisKey, secondsUntilMidnight);
            }

            return null;
        });
    }

}
