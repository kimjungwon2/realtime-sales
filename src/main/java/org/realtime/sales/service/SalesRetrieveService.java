package org.realtime.sales.service;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SalesRetrieveService {
    private final RedisTemplate<String, Object> redisTemplate;

    public SalesRetrieveService(RedisTemplate<String, Object> redisTemplate) {
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
}
