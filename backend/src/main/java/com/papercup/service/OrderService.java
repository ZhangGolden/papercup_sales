package com.papercup.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.papercup.dto.OrderRequest;
import com.papercup.entity.CartItem;
import com.papercup.entity.OrderInfo;
import com.papercup.entity.OrderItem;
import com.papercup.entity.Product;
import com.papercup.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 订单服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderInfoMapper orderInfoMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;

    private static final AtomicLong orderSequence = new AtomicLong(0);

    /**
     * 创建订单
     */
    @Transactional
    public OrderInfo createOrder(Long userId, OrderRequest request) {
        // 获取购物车商品
        List<CartItem> cartItems = cartItemMapper.selectBatchIds(request.getCartItemIds());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("购物车为空");
        }

        // 验证购物车商品是否属于当前用户
        boolean allBelongsToUser = cartItems.stream()
                .allMatch(item -> item.getUserId().equals(userId));
        if (!allBelongsToUser) {
            throw new RuntimeException("购物车商品验证失败");
        }

        // 计算订单总金额并验证库存
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = productMapper.selectById(cartItem.getProductId());
            if (product == null || product.getStatus() == 0) {
                throw new RuntimeException("商品 " + cartItem.getProductId() + " 不存在或已下架");
            }
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("商品 " + product.getName() + " 库存不足");
            }
            
            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // 创建订单
        OrderInfo order = new OrderInfo();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setActualAmount(totalAmount);
        order.setStatus("PENDING");
        order.setPaymentStatus("UNPAID");
        order.setPaymentMethod(request.getPaymentMethod());
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getFullAddress());
        order.setRemark(request.getRemark());

        orderInfoMapper.insert(order);

        // 创建订单项并更新库存
        for (CartItem cartItem : cartItems) {
            Product product = productMapper.selectById(cartItem.getProductId());
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(getFirstImage(product.getImages()));
            orderItem.setProductPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(product.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
            orderItem.setCustomOptions(cartItem.getCustomOptions());
            
            orderItemMapper.insert(orderItem);

            // 更新产品库存和销量
            product.setStock(product.getStock() - cartItem.getQuantity());
            product.setSales(product.getSales() + cartItem.getQuantity());
            productMapper.updateById(product);
        }

        // 删除购物车中的商品
        cartItemMapper.deleteBatchIds(request.getCartItemIds());

        log.info("用户 {} 创建订单成功: {}", userId, order.getOrderNo());
        return order;
    }

    /**
     * 获取用户订单列表
     */
    public IPage<OrderInfo> getUserOrders(Long userId, Integer current, Integer size) {
        Page<OrderInfo> page = new Page<>(current, size);
        LambdaQueryWrapper<OrderInfo> query = new LambdaQueryWrapper<>();
        query.eq(OrderInfo::getUserId, userId)
             .orderByDesc(OrderInfo::getCreatedTime);
        return orderInfoMapper.selectPage(page, query);
    }

    /**
     * 获取订单详情
     */
    public OrderInfo getOrderDetail(Long userId, Long orderId) {
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }

        // 获取订单项
        LambdaQueryWrapper<OrderItem> query = new LambdaQueryWrapper<>();
        query.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> orderItems = orderItemMapper.selectList(query);
        
        // 这里可以扩展为包含订单项的VO类
        return order;
    }

    /**
     * 取消订单
     */
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }

        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("只能取消待付款订单");
        }

        // 恢复库存
        LambdaQueryWrapper<OrderItem> query = new LambdaQueryWrapper<>();
        query.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> orderItems = orderItemMapper.selectList(query);

        for (OrderItem orderItem : orderItems) {
            Product product = productMapper.selectById(orderItem.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + orderItem.getQuantity());
                product.setSales(product.getSales() - orderItem.getQuantity());
                productMapper.updateById(product);
            }
        }

        // 更新订单状态
        order.setStatus("CANCELLED");
        orderInfoMapper.updateById(order);

        log.info("用户 {} 取消订单: {}", userId, order.getOrderNo());
    }

    /**
     * 模拟支付
     */
    @Transactional
    public void payOrder(Long userId, Long orderId) {
        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }

        if (!"PENDING".equals(order.getStatus()) || !"UNPAID".equals(order.getPaymentStatus())) {
            throw new RuntimeException("订单状态异常");
        }

        // 更新订单状态
        order.setStatus("PAID");
        order.setPaymentStatus("PAID");
        order.setPaymentTime(LocalDateTime.now());
        orderInfoMapper.updateById(order);

        log.info("用户 {} 支付订单成功: {}", userId, order.getOrderNo());
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long sequence = orderSequence.incrementAndGet() % 10000;
        return "PC" + timestamp + String.format("%04d", sequence);
    }

    /**
     * 获取第一张图片
     */
    private String getFirstImage(String images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        // 简单解析JSON数组的第一个元素
        images = images.trim();
        if (images.startsWith("[") && images.endsWith("]")) {
            String content = images.substring(1, images.length() - 1);
            if (!content.isEmpty()) {
                String[] imageArray = content.split(",");
                if (imageArray.length > 0) {
                    return imageArray[0].trim().replaceAll("\"", "");
                }
            }
        }
        return null;
    }
}
