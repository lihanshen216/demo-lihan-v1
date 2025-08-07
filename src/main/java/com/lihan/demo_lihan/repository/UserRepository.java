package com.lihan.demo_lihan.repository;

import com.lihan.demo_lihan.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据手机号查找用户
     */
    Optional<User> findByPhone(String phone);

    /**
     * 根据用户名或邮箱查找用户
     */
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    /**
     * 查找所有启用的用户
     */
    List<User> findByIsEnabledTrue();

    /**
     * 查找所有未锁定的用户
     */
    List<User> findByIsLockedFalse();

    /**
     * 查找启用且未锁定的用户
     */
    List<User> findByIsEnabledTrueAndIsLockedFalse();

    /**
     * 根据角色查找用户
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleCode = :roleCode")
    List<User> findByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 分页查询用户（支持关键字搜索）
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "u.username LIKE %:keyword% OR " +
           "u.nickname LIKE %:keyword% OR " +
           "u.email LIKE %:keyword%)")
    Page<User> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据创建时间范围查找用户
     */
    List<User> findByCreatedTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计用户总数
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isEnabled = true")
    long countEnabledUsers();

    /**
     * 检查用户名是否已存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否已存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否已存在
     */
    boolean existsByPhone(String phone);

    /**
     * 查找拥有指定课程的用户
     */
    @Query("SELECT u FROM User u JOIN u.enrolledCourses c WHERE c.id = :courseId")
    List<User> findByCourseId(@Param("courseId") Long courseId);
}
