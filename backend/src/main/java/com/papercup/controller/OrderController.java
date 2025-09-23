package com.papercup.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.papercup.common.Result;
import com.papercup.dto.OrderRequest;
import com.papercup.entity.OrderInfo;
import com.papercup.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    public Result<OrderInfo> createOrder(@Valid @RequestBody OrderRequest request,
                                        HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            if (userId == null) {
                return Result.unauthorized("请先登录");
            }

            OrderInfo order = orderService.createOrder(userId, request);
            return Result.success("订单创建成功", order);
        } catch (Exception e) {
            log.error("创建订单失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping("/list")
    public Result<IPage<OrderInfo>> getUserOrders(@RequestParam(defaultValue = "1") Integer current,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.unauthorized("请先登录");
            }

            IPage<OrderInfo> orders = orderService.getUserOrders(userId, current, size);
            return Result.success(orders);
        } catch (Exception e) {
            log.error("获取订单列表失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public Result<OrderInfo> getOrderDetail(@PathVariable Long orderId,
                                           HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.unauthorized("请先登录");
            }

            OrderInfo order = orderService.getOrderDetail(userId, orderId);
            return Result.success(order);
        } catch (Exception e) {
            log.error("获取订单详情失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消订单
     */
    @PutMapping("/{orderId}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long orderId,
                                   HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.unauthorized("请先登录");
            }

            orderService.cancelOrder(userId, orderId);
            return Result.success("取消订单成功");
        } catch (Exception e) {
            log.error("取消订单失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 支付订单 (模拟)
     */
    @PutMapping("/{orderId}/pay")
    public Result<Void> payOrder(@PathVariable Long orderId,
                                HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.unauthorized("请先登录");
            }

            orderService.payOrder(userId, orderId);
            return Result.success("支付成功");
        } catch (Exception e) {
            log.error("支付订单失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
