package org.realtime.sales.service;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SalesManageService {
    private final RedisTemplate<String, Object> redisTemplate;

    public SalesManageService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getSalesValue(String terminalId, String paymentMethod) {
        String dateKey = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String key = "terminal:" + terminalId;
        String field = "date:" + dateKey + ":method:" + paymentMethod + ":dailySales"; // 필드 키 생성

        return redisTemplate.execute((RedisCallback<String>) (connection) -> {
            byte[] redisKey = redisTemplate.getStringSerializer().serialize(key);
            byte[] redisField = redisTemplate.getStringSerializer().serialize(field);

            byte[] valueBytes = connection.hGet(redisKey, redisField);

            return valueBytes != null ? redisTemplate.getStringSerializer().deserialize(valueBytes) : null;
        });
    }

    void cleanUpRedis(String terminalId) {
        String paymentMethod = "CARD"; // 테스트에서 사용한 paymentMethod
        String dateKey = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));

        // Redis 키 및 필드 생성
        String key = "terminal:" + terminalId;
        String field = "date:" + dateKey + ":method:" + paymentMethod + ":dailySales";

        redisTemplate.opsForHash().delete(key, field);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(key)) &&
                redisTemplate.opsForHash().size(key) == 0) {
            redisTemplate.delete(key);
        }
    }
}
