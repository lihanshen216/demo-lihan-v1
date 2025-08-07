package com.lihan.demo_lihan.controller;

import com.lihan.demo_lihan.common.BusinessException;
import com.lihan.demo_lihan.common.Constants;
import com.lihan.demo_lihan.common.Result;
import com.lihan.demo_lihan.dto.LoginRequest;
import com.lihan.demo_lihan.dto.RegisterRequest;
import com.lihan.demo_lihan.entity.User;
import com.lihan.demo_lihan.service.RateLimiterService;
import com.lihan.demo_lihan.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(Constants.Api.API_PREFIX + "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final RateLimiterService rateLimiterService;

    /**
     * 测试接口，任何登录用户都能访问
     */
    @GetMapping("/hello")
    @PreAuthorize("isAuthenticated()")
    public Result<String> hello(@RequestHeader("username") String username) {
        // 调用限流判断
        boolean allowed = rateLimiterService.tryAcquire("rate_limit:" + username);
        if (!allowed) {
            throw new BusinessException("请求过于频繁，请稍后再试");
        }
        return Result.success("Hello from UserController! 用户管理模块运行正常。");
    }
    /**
     * 用户注册，公开接口，不需要权限
     */
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return Result.success(user, "用户注册成功");
    }

    /**
     * 登录接口，公开接口，不需要权限
     */
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        String jwt = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return Result.success(jwt, "登录成功");
    }

    /**
     * 查询所有用户，只有管理员角色能访问
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return Result.success(users);
    }

    /**
     * 分页查询用户，管理员和教师可以访问
     */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Page<User>> getUsersPage(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "createdTime") String sort,
            @RequestParam(value = "direction", defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<User> users = userService.findByKeyword(keyword, pageable);
        return Result.success(users);
    }

    /**
     * 根据ID获取用户信息，用户本人或管理员可访问
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return Result.success(user);
    }

    /**
     * 创建用户，只允许管理员
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return Result.success(createdUser, "用户创建成功");
    }

    /**
     * 更新用户信息，用户本人或管理员
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public Result<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return Result.success(updatedUser, "用户信息更新成功");
    }

    /**
     * 修改密码，用户本人
     */
    @PutMapping("/{id}/password")
    @PreAuthorize("#id == principal.id")
    public Result<Void> changePassword(
            @PathVariable Long id,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {

        userService.changePassword(id, oldPassword, newPassword);
        return Result.success(null, "密码修改成功");
    }

    /**
     * 启用/禁用用户，只允许管理员
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> toggleUserStatus(
            @PathVariable Long id,
            @RequestParam("enabled") boolean enabled) {

        userService.toggleUserStatus(id, enabled);
        String message = enabled ? "用户已启用" : "用户已禁用";
        return Result.success(null, message);
    }

    /**
     * 锁定/解锁用户，只允许管理员
     */
    @PutMapping("/{id}/lock")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> toggleUserLock(
            @PathVariable Long id,
            @RequestParam("locked") boolean locked) {

        userService.toggleUserLock(id, locked);
        String message = locked ? "用户已锁定" : "用户已解锁";
        return Result.success(null, message);
    }

    /**
     * 删除用户，只允许管理员
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success(null, "用户删除成功");
    }

    /**
     * 获取用户统计信息，管理员可访问
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> getUserStats() {
        long totalUsers = userService.findAll().size();
        return Result.success("用户总数: " + totalUsers);
    }
    // 管理员测试接口
    @GetMapping("/admin/test")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> adminTest() {
        return Result.success("只有管理员能访问此接口");
    }

    // 教师测试接口
    @GetMapping("/teacher/test")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<String> teacherTest() {
        return Result.success("只有教师能访问此接口");
    }

    // 学生测试接口
    @GetMapping("/student/test")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<String> studentTest() {
        return Result.success("只有学生能访问此接口");
    }
}
