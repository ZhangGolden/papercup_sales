package com.papercup.controller;

import com.papercup.common.Result;
import com.papercup.dto.PaymentRequest;
import com.papercup.dto.PaymentResponse;
import com.papercup.entity.PaymentOrder;
import com.papercup.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器
 */
@Slf4j
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 创建支付订单
     */
    @PostMapping("/create")
    public Result<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request,
                                               HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            if (userId == null) {
                return Result.unauthorized("请先登录");
            }

            // 设置客户端IP
            request.setClientIp(getClientIp(httpRequest));

            PaymentResponse response = paymentService.createPayment(userId, request);
            
            if (response.getErrorMessage() != null) {
                return Result.error(response.getErrorMessage());
            }
            
            return Result.success("创建支付订单成功", response);
        } catch (Exception e) {
            log.error("创建支付订单失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询支付状态
     */
    @GetMapping("/status/{paymentOrderNo}")
    public Result<PaymentOrder> queryPaymentStatus(@PathVariable String paymentOrderNo,
                                                  HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            if (userId == null) {
                return Result.unauthorized("请先登录");
            }

            PaymentOrder paymentOrder = paymentService.queryPaymentStatus(paymentOrderNo);
            if (paymentOrder == null) {
                return Result.error("支付订单不存在");
            }

            // 验证订单所有权
            if (!paymentOrder.getUserId().equals(userId)) {
                return Result.forbidden("无权限访问该支付订单");
            }

            return Result.success(paymentOrder);
        } catch (Exception e) {
            log.error("查询支付状态失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消支付
     */
    @PostMapping("/cancel/{paymentOrderNo}")
    public Result<Void> cancelPayment(@PathVariable String paymentOrderNo,
                                     HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            if (userId == null) {
                return Result.unauthorized("请先登录");
            }

            // 验证订单所有权
            PaymentOrder paymentOrder = paymentService.queryPaymentStatus(paymentOrderNo);
            if (paymentOrder == null) {
                return Result.error("支付订单不存在");
            }
            
            if (!paymentOrder.getUserId().equals(userId)) {
                return Result.forbidden("无权限操作该支付订单");
            }

            boolean success = paymentService.cancelPayment(paymentOrderNo);
            if (success) {
                return Result.success("取消支付成功");
            } else {
                return Result.error("取消支付失败");
            }
        } catch (Exception e) {
            log.error("取消支付失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 微信支付回调
     */
    @PostMapping("/wechat/notify")
    public String wechatPaymentNotify(@RequestBody String callbackData) {
        try {
            log.info("收到微信支付回调: {}", callbackData);
            boolean success = paymentService.handlePaymentCallback("WECHAT", callbackData);
            
            if (success) {
                return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
            } else {
                return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[处理失败]]></return_msg></xml>";
            }
        } catch (Exception e) {
            log.error("处理微信支付回调失败: {}", e.getMessage(), e);
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[系统错误]]></return_msg></xml>";
        }
    }

    /**
     * 支付宝支付回调
     */
    @PostMapping("/alipay/notify")
    public String alipayPaymentNotify(@RequestBody String callbackData) {
        try {
            log.info("收到支付宝支付回调: {}", callbackData);
            boolean success = paymentService.handlePaymentCallback("ALIPAY", callbackData);
            
            if (success) {
                return "success";
            } else {
                return "fail";
            }
        } catch (Exception e) {
            log.error("处理支付宝支付回调失败: {}", e.getMessage(), e);
            return "fail";
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String xip = request.getHeader("X-Real-IP");
        String xfor = request.getHeader("X-Forwarded-For");
        
        if (xfor != null && !xfor.isEmpty() && !"unknown".equalsIgnoreCase(xfor)) {
            int index = xfor.indexOf(",");
            if (index != -1) {
                return xfor.substring(0, index);
            } else {
                return xfor;
            }
        }
        
        if (xip != null && !xip.isEmpty() && !"unknown".equalsIgnoreCase(xip)) {
            return xip;
        }
        
        return request.getRemoteAddr();
    }
}
