package com.lihan.demo_lihan.service;

import com.lihan.demo_lihan.common.BusinessException;
import com.lihan.demo_lihan.common.Utils;
import com.lihan.demo_lihan.entity.Order;
import com.lihan.demo_lihan.repository.OrderRepository;
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
public class OrderService {

    private final OrderRepository orderRepository;

    /**
     * 根据ID查找订单
     */
    public Optional<Order> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return orderRepository.findById(id);
    }

    /**
     * 根据订单号查找订单
     */
    public Optional<Order> findByOrderNumber(String orderNumber) {
        if (Utils.isEmpty(orderNumber)) {
            return Optional.empty();
        }
        return orderRepository.findByOrderNumber(orderNumber);
    }

    /**
     * 分页查询用户订单
     */
    public Page<Order> findByUserIdAndStatus(Long userId, String status, Pageable pageable) {
        return orderRepository.findByUserIdAndStatus(userId, status, pageable);
    }

    /**
     * 根据用户ID查找订单
     */
    public List<Order> findByUserId(Long userId) {
        if (userId == null) {
            return List.of();
        }
        return orderRepository.findByUserId(userId);
    }

    /**
     * 根据课程ID查找订单
     */
    public List<Order> findByCourseId(Long courseId) {
        if (courseId == null) {
            return List.of();
        }
        return orderRepository.findByCourseId(courseId);
    }

    /**
     * 创建订单
     */
    @Transactional
    public Order createOrder(Order order) {
        validateOrderForCreation(order);
        
        // 生成订单号
        if (Utils.isEmpty(order.getOrderNumber())) {
            order.setOrderNumber(generateOrderNumber());
        }
        
        // 设置默认状态
        if (Utils.isEmpty(order.getStatus())) {
            order.setStatus("PENDING");
        }
        
        // 设置订单过期时间（默认30分钟）
        if (order.getExpireTime() == null) {
            order.setExpireTime(LocalDateTime.now().plusMinutes(30));
        }

        Order savedOrder = orderRepository.save(order);
        log.info("创建订单成功: orderNumber={}, userId={}, courseId={}", 
                savedOrder.getOrderNumber(), savedOrder.getUserId(), savedOrder.getCourseId());
        return savedOrder;
    }

    /**
     * 支付订单
     */
    @Transactional
    public void payOrder(String orderNumber, String paymentMethod, String paymentNo) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new BusinessException("订单不存在"));
        
        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException("订单状态错误，无法支付");
        }
        
        if (order.getExpireTime() != null && order.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("订单已过期");
        }
        
        order.setStatus("PAID");
        order.setPaymentMethod(paymentMethod);
        order.setPaymentNo(paymentNo);
        order.setPayTime(LocalDateTime.now());
        
        orderRepository.save(order);
        log.info("订单支付成功: orderNumber={}, paymentMethod={}", orderNumber, paymentMethod);
    }

    /**
     * 取消订单
     */
    @Transactional
    public void cancelOrder(String orderNumber, String reason) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new BusinessException("订单不存在"));
        
        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException("订单状态错误，无法取消");
        }
        
        order.setStatus("CANCELLED");
        order.setCancelTime(LocalDateTime.now());
        if (Utils.isNotEmpty(reason)) {
            order.setRemark(reason);
        }
        
        orderRepository.save(order);
        log.info("订单取消成功: orderNumber={}, reason={}", orderNumber, reason);
    }

    /**
     * 退款订单
     */
    @Transactional
    public void refundOrder(String orderNumber, String reason) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new BusinessException("订单不存在"));
        
        if (!"PAID".equals(order.getStatus())) {
            throw new BusinessException("订单状态错误，无法退款");
        }
        
        order.setStatus("REFUNDED");
        order.setRefundTime(LocalDateTime.now());
        if (Utils.isNotEmpty(reason)) {
            order.setRemark(reason);
        }
        
        orderRepository.save(order);
        log.info("订单退款成功: orderNumber={}, reason={}", orderNumber, reason);
    }

    /**
     * 检查用户是否已购买课程
     */
    public boolean hasUserPurchasedCourse(Long userId, Long courseId) {
        if (userId == null || courseId == null) {
            return false;
        }
        return orderRepository.existsByUserIdAndCourseIdAndPaid(userId, courseId);
    }

    /**
     * 处理过期订单
     */
    @Transactional
    public void handleExpiredOrders() {
        List<Order> expiredOrders = orderRepository.findExpiredPendingOrders(LocalDateTime.now());
        for (Order order : expiredOrders) {
            order.setStatus("EXPIRED");
            orderRepository.save(order);
            log.info("订单已过期: orderNumber={}", order.getOrderNumber());
        }
    }

    /**
     * 计算用户总消费
     */
    public BigDecimal calculateUserTotalSpending(Long userId) {
        if (userId == null) {
            return BigDecimal.ZERO;
        }
        return orderRepository.calculateUserTotalSpending(userId);
    }

    /**
     * 验证订单创建信息
     */
    private void validateOrderForCreation(Order order) {
        if (order == null) {
            throw new BusinessException("订单信息不能为空");
        }
        
        if (order.getUserId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        if (order.getCourseId() == null) {
            throw new BusinessException("课程ID不能为空");
        }
        
        if (order.getAmount() == null) {
            throw new BusinessException("订单金额不能为空");
        }
        
        if (order.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("订单金额不能为负数");
        }
        
        // 检查是否已购买过该课程
        if (hasUserPurchasedCourse(order.getUserId(), order.getCourseId())) {
            throw new BusinessException("您已购买过该课程");
        }
    }

    /**
     * 生成订单号
     */
    private String generateOrderNumber() {
        return "ORDER" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
    }
}
