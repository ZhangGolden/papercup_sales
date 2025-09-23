package com.papercup.controller;

import com.papercup.common.Result;
import com.papercup.dto.CartItemRequest;
import com.papercup.entity.CartItem;
import com.papercup.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 */
@Slf4j
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 获取购物车列表
     */
    @GetMapping("/list")
    public Result<List<CartItem>> getCartItems(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.unauthorized("请先登录");
            }

            List<CartItem> cartItems = cartService.getCartItems(userId);
            return Result.success(cartItems);
        } catch (Exception e) {
            log.error("获取购物车列表失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    public Result<Void> addToCart(@Valid @RequestBody CartItemRequest request,
                                  HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            if (userId == null) {
                return Result.unauthorized("请先登录");
            }

            cartService.addToCart(userId, request);
            return Result.success("添加到购物车成功");
        } catch (Exception e) {
            log.error("添加到购物车失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/{cartItemId}")
    public Result<Void> updateCartItem(@PathVariable Long cartItemId,
                                      @RequestParam Integer quantity,
                                      HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.unauthorized("请先登录");
            }

            cartService.updateCartItem(userId, cartItemId, quantity);
            return Result.success("更新购物车成功");
        } catch (Exception e) {
            log.error("更新购物车失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/{cartItemId}")
    public Result<Void> removeCartItem(@PathVariable Long cartItemId,
                                      HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.unauthorized("请先登录");
            }

            cartService.removeCartItem(userId, cartItemId);
            return Result.success("删除购物车商品成功");
        } catch (Exception e) {
            log.error("删除购物车商品失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public Result<Void> clearCart(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.unauthorized("请先登录");
            }

            cartService.clearCart(userId);
            return Result.success("清空购物车成功");
        } catch (Exception e) {
            log.error("清空购物车失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取购物车商品数量
     */
    @GetMapping("/count")
    public Result<Long> getCartItemCount(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.unauthorized("请先登录");
            }

            Long count = cartService.getCartItemCount(userId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取购物车商品数量失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
