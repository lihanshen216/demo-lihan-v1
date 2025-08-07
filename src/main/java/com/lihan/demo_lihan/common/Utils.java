package com.lihan.demo_lihan.common;

import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * 通用工具类
 */
public class Utils {

    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return !StringUtils.hasText(str);
    }

    /**
     * 判断字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return StringUtils.hasText(str);
    }

    /**
     * 判断集合是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断集合是否不为空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * 验证邮箱格式
     */
    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 验证手机号格式
     */
    public static boolean isValidPhone(String phone) {
        return isNotEmpty(phone) && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 格式化时间
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DEFAULT_DATE_TIME_FORMATTER);
    }

    /**
     * 获取当前时间戳（毫秒）
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 生成用户默认昵称
     */
    public static String generateDefaultNickname(String username) {
        if (isEmpty(username)) {
            return "用户" + System.currentTimeMillis();
        }
        return "用户_" + username;
    }

    /**
     * 响应工具类 - 内部类
     */
    public static class ResponseUtils {

        public static <T> Result<T> success() {
            return Result.success();
        }

        public static <T> Result<T> success(T data) {
            return Result.success(data);
        }

        public static <T> Result<T> success(T data, String message) {
            Result<T> result = Result.success(data);
            result.setMessage(message);
            return result;
        }

        public static <T> Result<T> error(String message) {
            return Result.error(message);
        }

        public static <T> Result<T> error(Integer code, String message) {
            return Result.error(code, message);
        }

        public static <T> Result<T> error(ResultCode resultCode) {
            return Result.error(resultCode);
        }

        public static <T> Result<T> paramError(String message) {
            return Result.error(ResultCode.PARAM_ERROR.getCode(), message);
        }

        public static <T> Result<T> userNotFound() {
            return Result.error(ResultCode.USER_NOT_FOUND);
        }

        public static <T> Result<T> passwordError() {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }
    }


    // Utils.java 中新增
    public static void validatePasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            throw new BusinessException("密码长度至少8位");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new BusinessException("密码必须包含至少一个大写字母");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new BusinessException("密码必须包含至少一个小写字母");
        }
        if (!password.matches(".*\\d.*")) {
            throw new BusinessException("密码必须包含至少一个数字");
        }
    }

}
