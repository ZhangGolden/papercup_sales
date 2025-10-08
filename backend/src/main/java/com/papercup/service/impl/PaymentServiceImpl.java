package com.papercup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.papercup.dto.PaymentRequest;
import com.papercup.dto.PaymentResponse;
import com.papercup.entity.OrderInfo;
import com.papercup.entity.PaymentConfig;
import com.papercup.entity.PaymentOrder;
import com.papercup.mapper.OrderInfoMapper;
import com.papercup.mapper.PaymentConfigMapper;
import com.papercup.mapper.PaymentOrderMapper;
import com.papercup.service.PaymentService;
import com.papercup.service.payment.AlipayPaymentHandler;
import com.papercup.service.payment.WechatPaymentHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 支付服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentOrderMapper paymentOrderMapper;
    private final PaymentConfigMapper paymentConfigMapper;
    private final OrderInfoMapper orderInfoMapper;
    private final WechatPaymentHandler wechatPaymentHandler;
    private final AlipayPaymentHandler alipayPaymentHandler;

    @Override
    @Transactional
    public PaymentResponse createPayment(Long userId, PaymentRequest request) {
        try {
            // 验证订单是否存在且属于当前用户
            LambdaQueryWrapper<OrderInfo> orderQuery = new LambdaQueryWrapper<>();
            orderQuery.eq(OrderInfo::getOrderNo, request.getOrderNo())
                     .eq(OrderInfo::getUserId, userId);
            OrderInfo orderInfo = orderInfoMapper.selectOne(orderQuery);
            
            if (orderInfo == null) {
                return PaymentResponse.error("订单不存在");
            }
            
            if (!"PENDING".equals(orderInfo.getStatus())) {
                return PaymentResponse.error("订单状态不正确");
            }

            // 检查是否已有支付订单
            PaymentOrder existingPayment = paymentOrderMapper.selectByBusinessOrderNo(request.getOrderNo());
            if (existingPayment != null && "SUCCESS".equals(existingPayment.getStatus())) {
                return PaymentResponse.error("订单已支付");
            }

            // 获取支付配置
            PaymentConfig config = paymentConfigMapper.selectEnabledByPaymentType(request.getPaymentType());
            if (config == null) {
                return PaymentResponse.error("支付方式未配置或已禁用");
            }

            // 创建支付订单
            PaymentOrder paymentOrder = createPaymentOrder(userId, request);
            paymentOrderMapper.insert(paymentOrder);

            // 调用对应的支付处理器
            PaymentResponse response;
            switch (request.getPaymentType().toUpperCase()) {
                case "WECHAT":
                    response = wechatPaymentHandler.createPayment(paymentOrder, config, request);
                    break;
                case "ALIPAY":
                    response = alipayPaymentHandler.createPayment(paymentOrder, config, request);
                    break;
                default:
                    return PaymentResponse.error("不支持的支付方式");
            }

            // 更新支付订单信息
            if (response.getQrCode() != null || response.getPaymentUrl() != null) {
                paymentOrder.setStatus("PENDING");
                paymentOrderMapper.updateById(paymentOrder);
            }

            response.setPaymentOrderNo(paymentOrder.getPaymentOrderNo());
            return response;

        } catch (Exception e) {
            log.error("创建支付订单失败: {}", e.getMessage(), e);
            return PaymentResponse.error("创建支付订单失败: " + e.getMessage());
        }
    }

    @Override
    public PaymentOrder queryPaymentStatus(String paymentOrderNo) {
        return paymentOrderMapper.selectByPaymentOrderNo(paymentOrderNo);
    }

    @Override
    @Transactional
    public boolean handlePaymentCallback(String paymentType, String callbackData) {
        try {
            switch (paymentType.toUpperCase()) {
                case "WECHAT":
                    return wechatPaymentHandler.handleCallback(callbackData);
                case "ALIPAY":
                    return alipayPaymentHandler.handleCallback(callbackData);
                default:
                    log.error("不支持的支付方式: {}", paymentType);
                    return false;
            }
        } catch (Exception e) {
            log.error("处理支付回调失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean cancelPayment(String paymentOrderNo) {
        try {
            PaymentOrder paymentOrder = paymentOrderMapper.selectByPaymentOrderNo(paymentOrderNo);
            if (paymentOrder == null) {
                return false;
            }

            if ("SUCCESS".equals(paymentOrder.getStatus())) {
                return false; // 已支付的订单不能取消
            }

            paymentOrder.setStatus("CANCELLED");
            paymentOrderMapper.updateById(paymentOrder);
            return true;
        } catch (Exception e) {
            log.error("取消支付失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean refundPayment(String paymentOrderNo, String reason) {
        // TODO: 实现退款逻辑
        log.info("申请退款: paymentOrderNo={}, reason={}", paymentOrderNo, reason);
        return true;
    }

    /**
     * 创建支付订单
     */
    private PaymentOrder createPaymentOrder(Long userId, PaymentRequest request) {
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setPaymentOrderNo(generatePaymentOrderNo());
        paymentOrder.setBusinessOrderNo(request.getOrderNo());
        paymentOrder.setUserId(userId);
        paymentOrder.setPaymentType(request.getPaymentType().toUpperCase());
        paymentOrder.setAmount(request.getAmount());
        paymentOrder.setSubject(request.getSubject());
        paymentOrder.setDescription(request.getDescription());
        paymentOrder.setStatus("PENDING");
        paymentOrder.setExpireTime(LocalDateTime.now().plusMinutes(30)); // 30分钟过期
        return paymentOrder;
    }

    /**
     * 生成支付订单号
     */
    private String generatePaymentOrderNo() {
        return "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 更新支付订单状态
     */
    @Transactional
    public void updatePaymentOrderStatus(String paymentOrderNo, String status, String thirdPartyOrderNo, String callbackData) {
        PaymentOrder paymentOrder = paymentOrderMapper.selectByPaymentOrderNo(paymentOrderNo);
        if (paymentOrder != null) {
            paymentOrder.setStatus(status);
            paymentOrder.setThirdPartyOrderNo(thirdPartyOrderNo);
            paymentOrder.setCallbackData(callbackData);
            
            if ("SUCCESS".equals(status)) {
                paymentOrder.setPaidTime(LocalDateTime.now());
                
                // 更新业务订单状态
                LambdaQueryWrapper<OrderInfo> query = new LambdaQueryWrapper<>();
                query.eq(OrderInfo::getOrderNo, paymentOrder.getBusinessOrderNo());
                OrderInfo orderInfo = orderInfoMapper.selectOne(query);
                if (orderInfo != null) {
                    orderInfo.setStatus("PAID");
                    orderInfo.setPaymentStatus("PAID");
                    orderInfo.setPaymentMethod(paymentOrder.getPaymentType());
                    orderInfo.setPaymentTime(LocalDateTime.now());
                    orderInfoMapper.updateById(orderInfo);
                }
            }
            
            paymentOrderMapper.updateById(paymentOrder);
        }
    }
}
