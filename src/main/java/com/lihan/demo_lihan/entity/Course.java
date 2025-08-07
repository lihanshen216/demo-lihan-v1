package com.lihan.demo_lihan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "edu_course", indexes = {
        @Index(name = "idx_teacher_id", columnList = "teacher_id"),
        @Index(name = "idx_category_id", columnList = "category_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_created_time", columnList = "created_time")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_image", length = 255)
    private String coverImage;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(name = "teacher_name", length = 50)
    private String teacherName;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "category_name", length = 50)
    private String categoryName;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "DRAFT"; // DRAFT, PUBLISHED, OFFLINE

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "student_count", nullable = false)
    private Integer studentCount = 0;

    @Column(name = "lesson_count", nullable = false)
    private Integer lessonCount = 0;

    @Column(name = "duration", nullable = false)
    private Integer duration = 0; // 课程总时长（分钟）

    @Column(name = "level", length = 20)
    private String level = "BEGINNER"; // BEGINNER, INTERMEDIATE, ADVANCED

    @Column(name = "tags", length = 255)
    private String tags; // 课程标签，逗号分隔

    @Column(name = "is_free", nullable = false)
    private Boolean isFree = false;

    @Column(name = "is_hot", nullable = false)
    private Boolean isHot = false;

    @Column(name = "is_recommended", nullable = false)
    private Boolean isRecommended = false;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @UpdateTimestamp
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "published_time")
    private LocalDateTime publishedTime;

    // 多对多关系：一个课程可以被多个用户选择
    @ManyToMany(mappedBy = "enrolledCourses")
    private Set<User> enrolledUsers;

    // 课程状态枚举
    public enum CourseStatus {
        DRAFT("DRAFT", "草稿"),
        PUBLISHED("PUBLISHED", "已发布"),
        OFFLINE("OFFLINE", "已下架");

        private final String code;
        private final String name;

        CourseStatus(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

    // 课程难度枚举
    public enum CourseLevel {
        BEGINNER("BEGINNER", "初级"),
        INTERMEDIATE("INTERMEDIATE", "中级"),
        ADVANCED("ADVANCED", "高级");

        private final String code;
        private final String name;

        CourseLevel(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }
}
