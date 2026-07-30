package com.todo.controller;

import com.todo.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisTestController {
    private final StringRedisTemplate stringRedisTemplate;

    @GetMapping("/ping")
    public Result<String> ping() {
        String key = "todo:redis:test";
        String value = "hello redis";
        stringRedisTemplate.opsForValue().set(key, value, Duration.ofMinutes(5));
        String savedValue = stringRedisTemplate.opsForValue().get(key);
        return Result.success("Redis连接成功", savedValue);
    }
}
