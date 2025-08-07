package com.lihan.demo_lihan.common;

/**
 * 系统常量类
 */
public class Constants {

    /**
     * 用户相关常量
     */
    public static class User {
        public static final String DEFAULT_PASSWORD = "123456";
        public static final String PASSWORD_SALT = "edu_platform_salt";
        public static final int USERNAME_MIN_LENGTH = 3;
        public static final int USERNAME_MAX_LENGTH = 20;
        public static final int PASSWORD_MIN_LENGTH = 6;
        public static final int PASSWORD_MAX_LENGTH = 20;
        public static final int NICKNAME_MAX_LENGTH = 30;
    }

    /**
     * 课程相关常量
     */
    public static class Course {
        public static final String STATUS_DRAFT = "DRAFT";
        public static final String STATUS_PUBLISHED = "PUBLISHED";
        public static final String STATUS_OFFLINE = "OFFLINE";
        public static final int TITLE_MAX_LENGTH = 100;
        public static final int DESCRIPTION_MAX_LENGTH = 1000;
    }

    /**
     * 订单相关常量
     */
    public static class Order {
        public static final String STATUS_PENDING = "PENDING";
        public static final String STATUS_PAID = "PAID";
        public static final String STATUS_CANCELLED = "CANCELLED";
        public static final String STATUS_REFUNDED = "REFUNDED";
        public static final int ORDER_NO_LENGTH = 20;
    }

    /**
     * 文件上传相关常量
     */
    public static class FileUpload {
        public static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB
        public static final long MAX_VIDEO_SIZE = 500 * 1024 * 1024; // 500MB
        public static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB
        public static final String[] ALLOWED_IMAGE_TYPES = {"jpg", "jpeg", "png", "gif", "webp"};
        public static final String[] ALLOWED_VIDEO_TYPES = {"mp4", "avi", "mov", "wmv", "flv"};
        public static final String UPLOAD_PATH = "/uploads/";
        public static final String IMAGE_PATH = "/uploads/images/";
        public static final String VIDEO_PATH = "/uploads/videos/";
    }

    /**
     * 缓存相关常量
     */
    public static class Cache {
        public static final String USER_CACHE_PREFIX = "user:";
        public static final String COURSE_CACHE_PREFIX = "course:";
        public static final String HOT_COURSES_KEY = "hot_courses";
        public static final int DEFAULT_EXPIRE_TIME = 3600; // 1小时
        public static final int USER_CACHE_EXPIRE_TIME = 1800; // 30分钟
        public static final int COURSE_CACHE_EXPIRE_TIME = 3600; // 1小时
    }

    /**
     * API相关常量
     */
    public static class Api {
        public static final String API_VERSION = "v1";
        public static final String API_PREFIX = "/api/" + API_VERSION;
        public static final int DEFAULT_PAGE_SIZE = 10;
        public static final int MAX_PAGE_SIZE = 100;
        public static final int DEFAULT_PAGE = 1;
    }

    /**
     * 时间相关常量
     */
    public static class Time {
        public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
        public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
        public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
        public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    }

    /**
     * 正则表达式常量
     */
    public static class Regex {
        public static final String EMAIL = "^[A-Za-z0-9+_.-]+@(.+)$";
        public static final String PHONE = "^1[3-9]\\d{9}$";
        public static final String USERNAME = "^[a-zA-Z0-9_]{3,20}$";
        public static final String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,20}$";
    }

    /**
     * 系统配置常量
     */
    public static class System {
        public static final String SYSTEM_NAME = "在线教育平台";
        public static final String SYSTEM_VERSION = "1.0.0";
        public static final String SYSTEM_AUTHOR = "demo-lihan";
        public static final String SYSTEM_EMAIL = "admin@eduplatform.com";
    }
}
