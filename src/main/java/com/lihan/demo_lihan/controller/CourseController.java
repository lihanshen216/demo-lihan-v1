package com.lihan.demo_lihan.controller;

import com.lihan.demo_lihan.common.Constants;
import com.lihan.demo_lihan.common.Result;
import com.lihan.demo_lihan.entity.Course;
import com.lihan.demo_lihan.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(Constants.Api.API_PREFIX + "/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * Hello World 接口 - 课程模块测试
     */
    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("Hello from CourseController! 课程管理模块运行正常。");
    }

    /**
     * 分页查询课程
     */
    @GetMapping
    public Result<Page<Course>> getCourses(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> courses = courseService.findByKeywordAndStatus(keyword, status, pageable);
        return Result.success(courses);
    }

    /**
     * 获取已发布的课程
     */
    @GetMapping("/published")
    public Result<List<Course>> getPublishedCourses() {
        List<Course> courses = courseService.findPublishedCourses();
        return Result.success(courses);
    }

    /**
     * 获取热门课程
     */
    @GetMapping("/hot")
    public Result<List<Course>> getHotCourses() {
        List<Course> courses = courseService.findHotCourses();
        return Result.success(courses);
    }

    /**
     * 根据ID获取课程信息
     */
    @GetMapping("/{id}")
    public Result<Course> getCourseById(@PathVariable Long id) {
        Course course = courseService.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
        
        // 增加浏览量
        courseService.incrementViewCount(id);
        
        return Result.success(course);
    }

    /**
     * 创建课程
     */
    @PostMapping
    public Result<Course> createCourse(@Valid @RequestBody Course course) {
        Course createdCourse = courseService.createCourse(course);
        return Result.success(createdCourse, "课程创建成功");
    }

    /**
     * 更新课程信息
     */
    @PutMapping("/{id}")
    public Result<Course> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        Course updatedCourse = courseService.updateCourse(id, course);
        return Result.success(updatedCourse, "课程信息更新成功");
    }

    /**
     * 发布课程
     */
    @PutMapping("/{id}/publish")
    public Result<Void> publishCourse(@PathVariable Long id) {
        courseService.publishCourse(id);
        return Result.success(null, "课程发布成功");
    }

    /**
     * 下架课程
     */
    @PutMapping("/{id}/offline")
    public Result<Void> offlineCourse(@PathVariable Long id) {
        courseService.offlineCourse(id);
        return Result.success(null, "课程下架成功");
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return Result.success(null, "课程删除成功");
    }
}
