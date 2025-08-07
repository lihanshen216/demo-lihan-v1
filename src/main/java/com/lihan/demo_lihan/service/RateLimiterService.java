package com.lihan.demo_lihan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;

    private static final int MAX_REQUESTS = 10; // 最大请求次数
    private static final Duration TIME_WINDOW = Duration.ofMinutes(1); // 时间窗口

    /**
     * 判断是否超出请求限制
     * @param key 唯一标识，比如用户名或IP
     * @return true 表示允许访问，false表示限流
     */
    public boolean tryAcquire(String key) {
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == 1) {
            redisTemplate.expire(key, TIME_WINDOW);
        }
        return count <= MAX_REQUESTS;
    }

    /**
     * 清除计数（可选）
     */
    public void reset(String key) {
        redisTemplate.delete(key);
    }
}
