package com.papercup.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.papercup.dto.CartItemRequest;
import com.papercup.entity.CartItem;
import com.papercup.entity.Product;
import com.papercup.mapper.CartItemMapper;
import com.papercup.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 购物车服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;

    /**
     * 获取用户购物车列表
     */
    public List<CartItem> getCartItems(Long userId) {
        return cartItemMapper.selectCartItemsWithProduct(userId);
    }

    /**
     * 添加商品到购物车
     */
    @Transactional
    public void addToCart(Long userId, CartItemRequest request) {
        // 检查产品是否存在且上架
        Product product = productMapper.selectById(request.getProductId());
        if (product == null || product.getStatus() == 0) {
            throw new RuntimeException("产品不存在或已下架");
        }

        // 检查库存
        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("库存不足");
        }

        // 查看购物车中是否已存在该商品
        LambdaQueryWrapper<CartItem> query = new LambdaQueryWrapper<>();
        query.eq(CartItem::getUserId, userId)
             .eq(CartItem::getProductId, request.getProductId());
        CartItem existingItem = cartItemMapper.selectOne(query);

        if (existingItem != null) {
            // 更新数量
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (newQuantity > product.getStock()) {
                throw new RuntimeException("库存不足");
            }
            existingItem.setQuantity(newQuantity);
            existingItem.setCustomOptions(request.getCustomOptions());
            cartItemMapper.updateById(existingItem);
        } else {
            // 新增购物车项
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(request.getProductId());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setCustomOptions(request.getCustomOptions());
            cartItemMapper.insert(cartItem);
        }

        log.info("用户 {} 添加商品到购物车: {}", userId, product.getName());
    }

    /**
     * 更新购物车商品数量
     */
    @Transactional
    public void updateCartItem(Long userId, Long cartItemId, Integer quantity) {
        // 查找购物车项
        CartItem cartItem = cartItemMapper.selectById(cartItemId);
        if (cartItem == null || !cartItem.getUserId().equals(userId)) {
            throw new RuntimeException("购物车项不存在");
        }

        // 检查产品库存
        Product product = productMapper.selectById(cartItem.getProductId());
        if (product == null || product.getStatus() == 0) {
            throw new RuntimeException("产品不存在或已下架");
        }

        if (quantity > product.getStock()) {
            throw new RuntimeException("库存不足");
        }

        // 更新数量
        cartItem.setQuantity(quantity);
        cartItemMapper.updateById(cartItem);

        log.info("用户 {} 更新购物车商品数量: {} -> {}", userId, product.getName(), quantity);
    }

    /**
     * 删除购物车商品
     */
    @Transactional
    public void removeCartItem(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemMapper.selectById(cartItemId);
        if (cartItem == null || !cartItem.getUserId().equals(userId)) {
            throw new RuntimeException("购物车项不存在");
        }

        cartItemMapper.deleteById(cartItemId);
        log.info("用户 {} 删除购物车商品: {}", userId, cartItemId);
    }

    /**
     * 清空购物车
     */
    @Transactional
    public void clearCart(Long userId) {
        LambdaQueryWrapper<CartItem> query = new LambdaQueryWrapper<>();
        query.eq(CartItem::getUserId, userId);
        cartItemMapper.delete(query);
        log.info("用户 {} 清空购物车", userId);
    }

    /**
     * 获取购物车商品数量
     */
    public Long getCartItemCount(Long userId) {
        LambdaQueryWrapper<CartItem> query = new LambdaQueryWrapper<>();
        query.eq(CartItem::getUserId, userId);
        return cartItemMapper.selectCount(query);
    }
}
