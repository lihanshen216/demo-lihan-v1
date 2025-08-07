package com.lihan.demo_lihan.controller;

import com.lihan.demo_lihan.common.Constants;
import com.lihan.demo_lihan.common.Result;
import com.lihan.demo_lihan.entity.Order;
import com.lihan.demo_lihan.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.Valid;
import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping(Constants.Api.API_PREFIX + "/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Hello World 接口 - 订单模块测试
     */
    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("Hello from OrderController! 订单管理模块运行正常。");
    }

    /**
     * 分页查询用户订单
     */
    @GetMapping("/user/{userId}")
    public Result<Page<Order>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderService.findByUserIdAndStatus(userId, status, pageable);
        return Result.success(orders);
    }

    /**
     * 根据ID获取订单信息
     */
    @GetMapping("/{id}")
    public Result<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        return Result.success(order);
    }

    /**
     * 根据订单号获取订单信息
     */
    @GetMapping("/number/{orderNumber}")
    public Result<Order> getOrderByNumber(@PathVariable String orderNumber) {
        Order order = orderService.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        return Result.success(order);
    }

    /**
     * 创建订单
     */
    @PostMapping
    public Result<Order> createOrder(@Valid @RequestBody Order order) {
        Order createdOrder = orderService.createOrder(order);
        return Result.success(createdOrder, "订单创建成功");
    }

    /**
     * 支付订单
     */
    @PutMapping("/{orderNumber}/pay")
    public Result<Void> payOrder(
            @PathVariable String orderNumber,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam("paymentNo") String paymentNo) {
        
        orderService.payOrder(orderNumber, paymentMethod, paymentNo);
        return Result.success(null, "订单支付成功");
    }

    /**
     * 取消订单
     */
    @PutMapping("/{orderNumber}/cancel")
    public Result<Void> cancelOrder(
            @PathVariable String orderNumber,
            @RequestParam(value = "reason", required = false) String reason) {
        
        orderService.cancelOrder(orderNumber, reason);
        return Result.success(null, "订单取消成功");
    }

    /**
     * 退款订单
     */
    @PutMapping("/{orderNumber}/refund")
    public Result<Void> refundOrder(
            @PathVariable String orderNumber,
            @RequestParam(value = "reason", required = false) String reason) {
        
        orderService.refundOrder(orderNumber, reason);
        return Result.success(null, "订单退款成功");
    }

    /**
     * 检查用户是否已购买课程
     */
    @GetMapping("/check")
    public Result<Boolean> checkUserPurchased(
            @RequestParam("userId") Long userId,
            @RequestParam("courseId") Long courseId) {
        
        boolean purchased = orderService.hasUserPurchasedCourse(userId, courseId);
        return Result.success(purchased);
    }

    /**
     * 获取用户总消费
     */
    @GetMapping("/user/{userId}/spending")
    public Result<BigDecimal> getUserTotalSpending(@PathVariable Long userId) {
        BigDecimal totalSpending = orderService.calculateUserTotalSpending(userId);
        return Result.success(totalSpending);
    }

    /**
     * 处理过期订单（系统调用）
     */
    @PostMapping("/handle-expired")
    public Result<Void> handleExpiredOrders() {
        orderService.handleExpiredOrders();
        return Result.success(null, "过期订单处理完成");
    }
}
