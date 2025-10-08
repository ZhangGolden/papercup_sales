package com.papercup.service;

import com.papercup.dto.PaymentRequest;
import com.papercup.dto.PaymentResponse;
import com.papercup.entity.PaymentOrder;

/**
 * 支付服务接口
 */
public interface PaymentService {

    /**
     * 创建支付订单
     */
    PaymentResponse createPayment(Long userId, PaymentRequest request);

    /**
     * 查询支付状态
     */
    PaymentOrder queryPaymentStatus(String paymentOrderNo);

    /**
     * 处理支付回调
     */
    boolean handlePaymentCallback(String paymentType, String callbackData);

    /**
     * 取消支付
     */
    boolean cancelPayment(String paymentOrderNo);

    /**
     * 申请退款
     */
    boolean refundPayment(String paymentOrderNo, String reason);
}
