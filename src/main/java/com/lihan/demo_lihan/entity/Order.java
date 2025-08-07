package com.lihan.demo_lihan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "edu_order", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_course_id", columnList = "course_id"),
        @Index(name = "idx_order_number", columnList = "order_number"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_created_time", columnList = "created_time")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "course_title", length = 100)
    private String courseTitle;

    @Column(name = "original_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "discount_price", precision = 10, scale = 2)
    private BigDecimal discountPrice;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // 实际支付金额

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING"; // PENDING, PAID, CANCELLED, REFUNDED

    @Column(name = "payment_method", length = 20)
    private String paymentMethod; // ALIPAY, WECHAT, BANK_CARD

    @Column(name = "payment_no", length = 100)
    private String paymentNo; // 第三方支付流水号

    @Column(name = "remark", length = 255)
    private String remark;

    @Column(name = "pay_time")
    private LocalDateTime payTime;

    @Column(name = "cancel_time")
    private LocalDateTime cancelTime;

    @Column(name = "refund_time")
    private LocalDateTime refundTime;

    @Column(name = "expire_time")
    private LocalDateTime expireTime; // 订单过期时间

    @CreationTimestamp
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @UpdateTimestamp
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    // 订单状态枚举
    public enum OrderStatus {
        PENDING("PENDING", "待支付"),
        PAID("PAID", "已支付"),
        CANCELLED("CANCELLED", "已取消"),
        REFUNDED("REFUNDED", "已退款"),
        EXPIRED("EXPIRED", "已过期");

        private final String code;
        private final String name;

        OrderStatus(String code, String name) {
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

    // 支付方式枚举
    public enum PaymentMethod {
        ALIPAY("ALIPAY", "支付宝"),
        WECHAT("WECHAT", "微信支付"),
        BANK_CARD("BANK_CARD", "银行卡");

        private final String code;
        private final String name;

        PaymentMethod(String code, String name) {
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
