package com.lihan.demo_lihan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final StringRedisTemplate redisTemplate;

    // 最大失败次数
    private static final int MAX_ATTEMPT = 5;

    // 失败次数计数过期时间，单位分钟
    private static final long LOCK_TIME_MINUTES = 5;

    // Redis key 前缀
    private static final String FAIL_KEY_PREFIX = "login:fail:";

    /**
     * 增加登录失败次数，返回当前失败次数
     */
    public int loginFailed(String username) {
        String key = FAIL_KEY_PREFIX + username;
        Integer attempts = getFailCount(username);
        if (attempts == null) attempts = 0;

        attempts++;
        redisTemplate.opsForValue().set(key, attempts.toString(), LOCK_TIME_MINUTES, TimeUnit.MINUTES);
        return attempts;
    }

    /**
     * 获取登录失败次数
     */
    public Integer getFailCount(String username) {
        String key = FAIL_KEY_PREFIX + username;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 判断用户是否被锁定（超过最大失败次数）
     */
    public boolean isBlocked(String username) {
        return getFailCount(username) >= MAX_ATTEMPT;
    }

    /**
     * 登录成功，清除失败次数
     */
    public void loginSucceeded(String username) {
        String key = FAIL_KEY_PREFIX + username;
        redisTemplate.delete(key);
    }
}
