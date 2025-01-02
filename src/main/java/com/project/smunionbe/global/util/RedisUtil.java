package com.project.smunionbe.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, Object> redisBlackListTemplate;
    private final RedisTemplate<String, Object> redisJsonTemplate; // 푸시 알림용

    public void save(String key, Object val, Long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, val, time, timeUnit);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }


    // BlackList
    public void setBlackList(String key, Object o, Long milliSeconds) {
        redisBlackListTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(o.getClass()));
        redisBlackListTemplate.opsForValue().set(key, o, milliSeconds, TimeUnit.MILLISECONDS);
    }

    public Object getBlackList(String key) {
        return redisBlackListTemplate.opsForValue().get(key);
    }

    public boolean deleteBlackList(String key) {
        return redisBlackListTemplate.delete(key);
    }

    public boolean hasKeyBlackList(String key) {
        return redisBlackListTemplate.hasKey(key);
    }

    // JSON 데이터 저장 (Object 타입, 예: FCM 토큰)
    public void saveJson(String key, Object value, Long time, TimeUnit timeUnit) {
        redisJsonTemplate.opsForValue().set(key, value, time, timeUnit);
    }

    public Object getJson(String key) {
        return redisJsonTemplate.opsForValue().get(key);
    }

    public boolean deleteJson(String key) {
        return Boolean.TRUE.equals(redisJsonTemplate.delete(key));
    }

    public boolean hasKeyJson(String key) {
        return Boolean.TRUE.equals(redisJsonTemplate.hasKey(key));
    }

}
