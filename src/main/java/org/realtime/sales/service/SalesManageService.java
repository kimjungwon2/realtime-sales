package org.realtime.sales.service;

import org.realtime.sales.service.dto.PaymentMethod;
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

    public Integer getSalesValue(String terminalId, PaymentMethod paymentMethod) {
        String dateKey = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String key = "terminal:" + terminalId;
        String field = "date:" + dateKey + ":method:" + paymentMethod + ":dailySales"; // 필드 키 생성

        return redisTemplate.execute((RedisCallback<Integer>) (connection) -> {
            byte[] redisKey = redisTemplate.getStringSerializer().serialize(key);
            byte[] redisField = redisTemplate.getStringSerializer().serialize(field);

            byte[] valueBytes = connection.hGet(redisKey, redisField);

            if (valueBytes != null) {
                String valueStr = redisTemplate.getStringSerializer().deserialize(valueBytes);
                try {
                    return Integer.parseInt(valueStr);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format for key: " + key + ", field: " + field);
                    return null;
                }
            }
            return null; // 값이 없으면 null 반환
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
