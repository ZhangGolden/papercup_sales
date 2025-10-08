package com.papercup.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付请求DTO
 */
@Data
public class PaymentRequest {

    /**
     * 业务订单号
     */
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    /**
     * 支付方式: WECHAT, ALIPAY
     */
    @NotBlank(message = "支付方式不能为空")
    private String paymentType;

    /**
     * 支付金额
     */
    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额必须大于0")
    private BigDecimal amount;

    /**
     * 支付标题
     */
    @NotBlank(message = "支付标题不能为空")
    private String subject;

    /**
     * 支付描述
     */
    private String description;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 支付成功跳转地址
     */
    private String returnUrl;
}
