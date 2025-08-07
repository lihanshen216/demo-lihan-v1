package com.lihan.demo_lihan.repository;

import com.lihan.demo_lihan.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 根据订单号查找订单
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * 根据用户ID查找订单
     */
    List<Order> findByUserId(Long userId);

    /**
     * 根据课程ID查找订单
     */
    List<Order> findByCourseId(Long courseId);

    /**
     * 根据订单状态查找订单
     */
    List<Order> findByStatus(String status);

    /**
     * 根据用户ID和订单状态查找订单
     */
    List<Order> findByUserIdAndStatus(Long userId, String status);

    /**
     * 根据用户ID和课程ID查找订单
     */
    Optional<Order> findByUserIdAndCourseId(Long userId, Long courseId);

    /**
     * 根据支付方式查找订单
     */
    List<Order> findByPaymentMethod(String paymentMethod);

    /**
     * 根据第三方支付流水号查找订单
     */
    Optional<Order> findByPaymentNo(String paymentNo);

    /**
     * 分页查询用户订单
     */
    @Query("SELECT o FROM Order o WHERE o.userId = :userId " +
           "AND (:status IS NULL OR o.status = :status) " +
           "ORDER BY o.createdTime DESC")
    Page<Order> findByUserIdAndStatus(@Param("userId") Long userId, 
                                     @Param("status") String status, 
                                     Pageable pageable);

    /**
     * 根据创建时间范围查找订单
     */
    List<Order> findByCreatedTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据支付时间范围查找订单
     */
    List<Order> findByPayTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查找待支付且已过期的订单
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' AND o.expireTime < :currentTime")
    List<Order> findExpiredPendingOrders(@Param("currentTime") LocalDateTime currentTime);

    /**
     * 统计用户订单数量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId AND o.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 统计课程销量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.courseId = :courseId AND o.status = 'PAID'")
    long countSalesByCourseId(@Param("courseId") Long courseId);

    /**
     * 统计各状态订单数量
     */
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();

    /**
     * 计算指定时间范围内的总收入
     */
    @Query("SELECT COALESCE(SUM(o.amount), 0) FROM Order o WHERE " +
           "o.status = 'PAID' AND o.payTime BETWEEN :startTime AND :endTime")
    BigDecimal calculateTotalRevenue(@Param("startTime") LocalDateTime startTime, 
                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 计算用户总消费金额
     */
    @Query("SELECT COALESCE(SUM(o.amount), 0) FROM Order o WHERE " +
           "o.userId = :userId AND o.status = 'PAID'")
    BigDecimal calculateUserTotalSpending(@Param("userId") Long userId);

    /**
     * 查找热销课程（根据已支付订单数量排序）
     */
    @Query("SELECT o.courseId, COUNT(o) as salesCount FROM Order o WHERE " +
           "o.status = 'PAID' GROUP BY o.courseId ORDER BY salesCount DESC")
    List<Object[]> findHotSellingCourses(Pageable pageable);

    /**
     * 检查用户是否已购买指定课程
     */
    @Query("SELECT COUNT(o) > 0 FROM Order o WHERE " +
           "o.userId = :userId AND o.courseId = :courseId AND o.status = 'PAID'")
    boolean existsByUserIdAndCourseIdAndPaid(@Param("userId") Long userId, 
                                           @Param("courseId") Long courseId);
}
