package com.lihan.demo_lihan.repository;

import com.lihan.demo_lihan.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * 根据课程状态查找课程
     */
    List<Course> findByStatus(String status);

    /**
     * 根据教师ID查找课程
     */
    List<Course> findByTeacherId(Long teacherId);

    /**
     * 根据分类ID查找课程
     */
    List<Course> findByCategoryId(Long categoryId);

    /**
     * 查找已发布的课程
     */
    List<Course> findByStatusOrderByCreatedTimeDesc(String status);

    /**
     * 查找热门课程
     */
    List<Course> findByIsHotTrueAndStatusOrderByViewCountDesc(String status);

    /**
     * 查找推荐课程
     */
    List<Course> findByIsRecommendedTrueAndStatusOrderBySortOrder(String status);

    /**
     * 查找免费课程
     */
    List<Course> findByIsFreeTrueAndStatus(String status);

    /**
     * 根据价格范围查找课程
     */
    List<Course> findByStatusAndPriceBetween(String status, BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * 根据难度级别查找课程
     */
    List<Course> findByStatusAndLevel(String status, String level);

    /**
     * 分页查询课程（支持关键字搜索）
     */
    @Query("SELECT c FROM Course c WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "c.title LIKE %:keyword% OR " +
           "c.description LIKE %:keyword% OR " +
           "c.teacherName LIKE %:keyword% OR " +
           "c.tags LIKE %:keyword%) " +
           "AND (:status IS NULL OR c.status = :status)")
    Page<Course> findByKeywordAndStatus(@Param("keyword") String keyword, 
                                       @Param("status") String status, 
                                       Pageable pageable);

    /**
     * 根据创建时间范围查找课程
     */
    List<Course> findByCreatedTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查找最受欢迎的课程（按学生数量排序）
     */
    @Query("SELECT c FROM Course c WHERE c.status = :status ORDER BY c.studentCount DESC")
    List<Course> findMostPopularCourses(@Param("status") String status, Pageable pageable);

    /**
     * 查找最新发布的课程
     */
    @Query("SELECT c FROM Course c WHERE c.status = :status AND c.publishedTime IS NOT NULL ORDER BY c.publishedTime DESC")
    List<Course> findLatestPublishedCourses(@Param("status") String status, Pageable pageable);

    /**
     * 统计各状态课程数量
     */
    @Query("SELECT c.status, COUNT(c) FROM Course c GROUP BY c.status")
    List<Object[]> countCoursesByStatus();

    /**
     * 统计教师的课程数量
     */
    @Query("SELECT COUNT(c) FROM Course c WHERE c.teacherId = :teacherId")
    long countByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 统计分类的课程数量
     */
    @Query("SELECT COUNT(c) FROM Course c WHERE c.categoryId = :categoryId AND c.status = :status")
    long countByCategoryIdAndStatus(@Param("categoryId") Long categoryId, @Param("status") String status);

    /**
     * 查找指定用户已选课程
     */
    @Query("SELECT c FROM Course c JOIN c.enrolledUsers u WHERE u.id = :userId")
    List<Course> findByUserId(@Param("userId") Long userId);

    /**
     * 更新课程浏览量
     */
    @Query("UPDATE Course c SET c.viewCount = c.viewCount + 1 WHERE c.id = :courseId")
    void incrementViewCount(@Param("courseId") Long courseId);

    /**
     * 更新课程学生数量
     */
    @Query("UPDATE Course c SET c.studentCount = c.studentCount + :increment WHERE c.id = :courseId")
    void updateStudentCount(@Param("courseId") Long courseId, @Param("increment") int increment);
}
