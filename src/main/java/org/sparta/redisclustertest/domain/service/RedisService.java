package org.sparta.redisclustertest.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.redisclustertest.domain.dto.CreateDto;
import org.sparta.redisclustertest.domain.dto.UpdateDto;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "StockRedisService")
@RequiredArgsConstructor
@Service
public class RedisService {

    private final RedisTemplate<String , Object> redisTemplate;

    private final String KEY_NAME = "stock";

    public void create(CreateDto createDto) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();

        String key = KEY_NAME + ":" + createDto.getId();

        hashOperations.put(key, "id", createDto.getId());
        hashOperations.put(key, "name", createDto.getName());
        hashOperations.put(key, "cnt", createDto.getCnt());
        hashOperations.getOperations().expire(key, 600, TimeUnit.SECONDS);
    }

    public void update(String id , UpdateDto updateDto) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        String key = KEY_NAME + ":" + id;

        Map<String, Object> entries = hashOperations.entries(key);

        if (!entries.isEmpty()) {
            int cnt = (int) entries.get("cnt") - updateDto.getCnt();

            hashOperations.put(key, "id", id);
            hashOperations.put(key, "name", (String) entries.get("name"));
            hashOperations.put(key, "cnt", cnt);
            hashOperations.getOperations().expire(key, 600, TimeUnit.SECONDS);
        }
    }

    public void delete(String id) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        String key = KEY_NAME + ":" + id;

        redisTemplate.delete(key);
    }

    public void select(String id) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        String key = KEY_NAME + ":" + id;

        Map<String, Object> entries = hashOperations.entries(key);
        if (!entries.isEmpty()) {
            log.info("Selected stock id: {}", entries.get("id"));
            log.info("Selected stock name: {}", entries.get("name"));
            log.info("Selected stock cnt: {}", entries.get("cnt"));
        }
    }
}
