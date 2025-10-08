package com.papercup.service.payment;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.papercup.dto.PaymentRequest;
import com.papercup.dto.PaymentResponse;
import com.papercup.entity.PaymentConfig;
import com.papercup.entity.PaymentOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝支付处理器
 */
@Slf4j
@Component
public class AlipayPaymentHandler {

    // 支付宝网关地址
    private static final String ALIPAY_GATEWAY_PROD = "https://openapi.alipay.com/gateway.do";
    private static final String ALIPAY_GATEWAY_SANDBOX = "https://openapi.alipaydev.com/gateway.do";

    /**
     * 创建支付宝支付订单
     */
    public PaymentResponse createPayment(PaymentOrder paymentOrder, PaymentConfig config, PaymentRequest request) {
        try {
            // 构建支付宝客户端
            AlipayClient alipayClient = buildAlipayClient(config);

            // 创建预下单请求
            AlipayTradePrecreateRequest alipayRequest = new AlipayTradePrecreateRequest();
            alipayRequest.setNotifyUrl(config.getNotifyUrl());
            alipayRequest.setReturnUrl(config.getReturnUrl());

            // 构建请求参数
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", paymentOrder.getPaymentOrderNo());
            bizContent.put("total_amount", paymentOrder.getAmount().toString());
            bizContent.put("subject", paymentOrder.getSubject());
            bizContent.put("body", paymentOrder.getDescription());
            bizContent.put("timeout_express", "30m"); // 30分钟超时

            alipayRequest.setBizContent(com.alibaba.fastjson.JSON.toJSONString(bizContent));

            // 调用支付宝API
            AlipayTradePrecreateResponse response = alipayClient.execute(alipayRequest);

            if (response.isSuccess()) {
                // 构建返回结果
                PaymentResponse paymentResponse = PaymentResponse.success(
                    paymentOrder.getPaymentOrderNo(),
                    "ALIPAY",
                    paymentOrder.getAmount()
                );
                paymentResponse.setQrCode(response.getQrCode()); // 二维码内容

                log.info("支付宝支付订单创建成功: {}", paymentOrder.getPaymentOrderNo());
                return paymentResponse;
            } else {
                log.error("支付宝支付订单创建失败: {}", response.getSubMsg());
                return PaymentResponse.error("创建支付宝订单失败: " + response.getSubMsg());
            }

        } catch (AlipayApiException e) {
            log.error("创建支付宝支付订单失败: {}", e.getMessage(), e);
            return PaymentResponse.error("创建支付宝支付订单失败: " + e.getMessage());
        }
    }

    /**
     * 处理支付宝支付回调
     */
    public boolean handleCallback(String callbackData) {
        try {
            // TODO: 实现支付宝支付回调验证和处理
            log.info("处理支付宝支付回调: {}", callbackData);
            
            // 这里需要根据支付宝回调的具体格式来解析
            // 验证签名、解析支付结果、更新订单状态等
            
            return true;
        } catch (Exception e) {
            log.error("处理支付宝支付回调失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 构建支付宝客户端
     */
    private AlipayClient buildAlipayClient(PaymentConfig config) {
        String serverUrl = config.getSandbox() == 1 ? ALIPAY_GATEWAY_SANDBOX : ALIPAY_GATEWAY_PROD;
        
        return new DefaultAlipayClient(
            serverUrl,
            config.getAppId(),
            config.getPrivateKey(),
            "json",
            "UTF-8",
            config.getPublicKey(),
            "RSA2"
        );
    }

    /**
     * 查询支付宝支付订单状态
     */
    public Map<String, Object> queryPaymentStatus(PaymentOrder paymentOrder, PaymentConfig config) {
        try {
            AlipayClient alipayClient = buildAlipayClient(config);
            
            // TODO: 调用支付宝查询接口
            Map<String, Object> result = new HashMap<>();
            result.put("status", "PENDING");
            return result;
            
        } catch (Exception e) {
            log.error("查询支付宝支付状态失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("status", "ERROR");
            result.put("message", e.getMessage());
            return result;
        }
    }
}
