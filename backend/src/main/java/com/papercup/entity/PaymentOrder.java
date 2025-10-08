package com.papercup.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付订单实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("payment_order")
public class PaymentOrder {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 支付订单号
     */
    @TableField("payment_order_no")
    private String paymentOrderNo;

    /**
     * 业务订单号
     */
    @TableField("business_order_no")
    private String businessOrderNo;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 支付方式: WECHAT, ALIPAY
     */
    @TableField("payment_type")
    private String paymentType;

    /**
     * 支付金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 支付标题
     */
    @TableField("subject")
    private String subject;

    /**
     * 支付描述
     */
    @TableField("description")
    private String description;

    /**
     * 第三方支付订单号
     */
    @TableField("third_party_order_no")
    private String thirdPartyOrderNo;

    /**
     * 支付状态: PENDING, SUCCESS, FAILED, CANCELLED
     */
    @TableField("status")
    private String status;

    /**
     * 支付时间
     */
    @TableField("paid_time")
    private LocalDateTime paidTime;

    /**
     * 过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 支付回调数据
     */
    @TableField("callback_data")
    private String callbackData;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
