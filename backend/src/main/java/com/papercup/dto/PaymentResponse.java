package com.papercup.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付响应DTO
 */
@Data
public class PaymentResponse {

    /**
     * 支付订单号
     */
    private String paymentOrderNo;

    /**
     * 支付方式
     */
    private String paymentType;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付状态
     */
    private String status;

    /**
     * 支付URL（用于跳转支付）
     */
    private String paymentUrl;

    /**
     * 二维码内容（用于扫码支付）
     */
    private String qrCode;

    /**
     * 支付参数（用于客户端调用）
     */
    private Map<String, Object> paymentParams;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 过期时间（分钟）
     */
    private Integer expireMinutes;

    public static PaymentResponse success(String paymentOrderNo, String paymentType, BigDecimal amount) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentOrderNo(paymentOrderNo);
        response.setPaymentType(paymentType);
        response.setAmount(amount);
        response.setStatus("PENDING");
        response.setExpireMinutes(30);
        return response;
    }

    public static PaymentResponse error(String errorMessage) {
        PaymentResponse response = new PaymentResponse();
        response.setStatus("FAILED");
        response.setErrorMessage(errorMessage);
        return response;
    }
}
