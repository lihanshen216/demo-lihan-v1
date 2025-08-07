package com.lihan.demo_lihan.service;

import com.lihan.demo_lihan.common.BusinessException;
import com.lihan.demo_lihan.common.Utils;
import com.lihan.demo_lihan.entity.Course;
import com.lihan.demo_lihan.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    /**
     * 根据ID查找课程
     */
    public Optional<Course> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return courseRepository.findById(id);
    }

    /**
     * 分页查询课程（支持关键字搜索）
     */
    public Page<Course> findByKeywordAndStatus(String keyword, String status, Pageable pageable) {
        return courseRepository.findByKeywordAndStatus(keyword, status, pageable);
    }

    /**
     * 获取所有已发布的课程
     */
    public List<Course> findPublishedCourses() {
        return courseRepository.findByStatusOrderByCreatedTimeDesc("PUBLISHED");
    }

    /**
     * 获取热门课程
     */
    public List<Course> findHotCourses() {
        return courseRepository.findByIsHotTrueAndStatusOrderByViewCountDesc("PUBLISHED");
    }

    /**
     * 获取推荐课程
     */
    public List<Course> findRecommendedCourses() {
        return courseRepository.findByIsRecommendedTrueAndStatusOrderBySortOrder("PUBLISHED");
    }

    /**
     * 根据教师ID查找课程
     */
    public List<Course> findByTeacherId(Long teacherId) {
        if (teacherId == null) {
            return List.of();
        }
        return courseRepository.findByTeacherId(teacherId);
    }

    /**
     * 根据分类ID查找课程
     */
    public List<Course> findByCategoryId(Long categoryId) {
        if (categoryId == null) {
            return List.of();
        }
        return courseRepository.findByCategoryId(categoryId);
    }

    /**
     * 创建课程
     */
    @Transactional
    public Course createCourse(Course course) {
        validateCourseForCreation(course);
        
        // 设置默认状态
        if (Utils.isEmpty(course.getStatus())) {
            course.setStatus("DRAFT");
        }
        
        // 设置默认值
        if (course.getViewCount() == null) {
            course.setViewCount(0);
        }
        if (course.getStudentCount() == null) {
            course.setStudentCount(0);
        }
        if (course.getLessonCount() == null) {
            course.setLessonCount(0);
        }
        if (course.getDuration() == null) {
            course.setDuration(0);
        }
        if (course.getIsFree() == null) {
            course.setIsFree(false);
        }
        if (course.getIsHot() == null) {
            course.setIsHot(false);
        }
        if (course.getIsRecommended() == null) {
            course.setIsRecommended(false);
        }
        if (course.getSortOrder() == null) {
            course.setSortOrder(0);
        }

        Course savedCourse = courseRepository.save(course);
        log.info("创建课程成功: title={}, id={}", savedCourse.getTitle(), savedCourse.getId());
        return savedCourse;
    }

    /**
     * 更新课程信息
     */
    @Transactional
    public Course updateCourse(Long id, Course courseUpdateInfo) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("课程不存在"));

        // 更新允许修改的字段
        if (Utils.isNotEmpty(courseUpdateInfo.getTitle())) {
            existingCourse.setTitle(courseUpdateInfo.getTitle());
        }
        if (Utils.isNotEmpty(courseUpdateInfo.getDescription())) {
            existingCourse.setDescription(courseUpdateInfo.getDescription());
        }
        if (Utils.isNotEmpty(courseUpdateInfo.getCoverImage())) {
            existingCourse.setCoverImage(courseUpdateInfo.getCoverImage());
        }
        if (courseUpdateInfo.getPrice() != null) {
            existingCourse.setPrice(courseUpdateInfo.getPrice());
        }
        if (courseUpdateInfo.getOriginalPrice() != null) {
            existingCourse.setOriginalPrice(courseUpdateInfo.getOriginalPrice());
        }
        if (Utils.isNotEmpty(courseUpdateInfo.getLevel())) {
            existingCourse.setLevel(courseUpdateInfo.getLevel());
        }
        if (Utils.isNotEmpty(courseUpdateInfo.getTags())) {
            existingCourse.setTags(courseUpdateInfo.getTags());
        }
        if (courseUpdateInfo.getIsFree() != null) {
            existingCourse.setIsFree(courseUpdateInfo.getIsFree());
        }

        Course savedCourse = courseRepository.save(existingCourse);
        log.info("更新课程信息成功: id={}, title={}", id, savedCourse.getTitle());
        return savedCourse;
    }

    /**
     * 发布课程
     */
    @Transactional
    public void publishCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("课程不存在"));
        
        course.setStatus("PUBLISHED");
        course.setPublishedTime(LocalDateTime.now());
        courseRepository.save(course);
        
        log.info("发布课程成功: id={}, title={}", courseId, course.getTitle());
    }

    /**
     * 下架课程
     */
    @Transactional
    public void offlineCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("课程不存在"));
        
        course.setStatus("OFFLINE");
        courseRepository.save(course);
        
        log.info("下架课程成功: id={}, title={}", courseId, course.getTitle());
    }

    /**
     * 增加课程浏览量
     */
    @Transactional
    public void incrementViewCount(Long courseId) {
        courseRepository.incrementViewCount(courseId);
        log.debug("课程浏览量+1: courseId={}", courseId);
    }

    /**
     * 删除课程
     */
    @Transactional
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("课程不存在"));
        
        courseRepository.delete(course);
        log.info("删除课程成功: id={}, title={}", courseId, course.getTitle());
    }

    /**
     * 验证课程创建信息
     */
    private void validateCourseForCreation(Course course) {
        if (course == null) {
            throw new BusinessException("课程信息不能为空");
        }
        
        if (Utils.isEmpty(course.getTitle())) {
            throw new BusinessException("课程标题不能为空");
        }
        
        if (course.getTeacherId() == null) {
            throw new BusinessException("教师ID不能为空");
        }
        
        if (course.getCategoryId() == null) {
            throw new BusinessException("课程分类不能为空");
        }
        
        if (course.getPrice() == null) {
            throw new BusinessException("课程价格不能为空");
        }
        
        if (course.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("课程价格不能为负数");
        }
    }
}
