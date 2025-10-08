package com.papercup.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.papercup.common.Result;
import com.papercup.entity.PaymentConfig;
import com.papercup.mapper.PaymentConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 支付配置管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/payment-config")
@RequiredArgsConstructor
public class PaymentConfigController {

    private final PaymentConfigMapper paymentConfigMapper;

    /**
     * 获取所有支付配置
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<PaymentConfig>> getPaymentConfigs() {
        try {
            LambdaQueryWrapper<PaymentConfig> query = new LambdaQueryWrapper<>();
            query.orderByDesc(PaymentConfig::getCreatedTime);
            List<PaymentConfig> configs = paymentConfigMapper.selectList(query);
            
            // 隐藏敏感信息
            configs.forEach(config -> {
                if (config.getPrivateKey() != null) {
                    config.setPrivateKey("***");
                }
                if (config.getApiKey() != null) {
                    config.setApiKey("***");
                }
            });
            
            return Result.success(configs);
        } catch (Exception e) {
            log.error("获取支付配置列表失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取支付配置详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PaymentConfig> getPaymentConfig(@PathVariable Long id) {
        try {
            PaymentConfig config = paymentConfigMapper.selectById(id);
            if (config == null) {
                return Result.error("支付配置不存在");
            }
            
            // 隐藏敏感信息
            if (config.getPrivateKey() != null) {
                config.setPrivateKey("***");
            }
            if (config.getApiKey() != null) {
                config.setApiKey("***");
            }
            
            return Result.success(config);
        } catch (Exception e) {
            log.error("获取支付配置详情失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建支付配置
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PaymentConfig> createPaymentConfig(@RequestBody PaymentConfig config) {
        try {
            // 检查是否已存在同类型的配置
            LambdaQueryWrapper<PaymentConfig> query = new LambdaQueryWrapper<>();
            query.eq(PaymentConfig::getPaymentType, config.getPaymentType());
            PaymentConfig existing = paymentConfigMapper.selectOne(query);
            
            if (existing != null) {
                return Result.error("该支付方式的配置已存在，请使用更新功能");
            }
            
            paymentConfigMapper.insert(config);
            return Result.success("创建支付配置成功", config);
        } catch (Exception e) {
            log.error("创建支付配置失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新支付配置
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PaymentConfig> updatePaymentConfig(@PathVariable Long id, @RequestBody PaymentConfig config) {
        try {
            PaymentConfig existing = paymentConfigMapper.selectById(id);
            if (existing == null) {
                return Result.error("支付配置不存在");
            }
            
            config.setId(id);
            
            // 如果私钥或API密钥为***，则保持原值不变
            if ("***".equals(config.getPrivateKey())) {
                config.setPrivateKey(existing.getPrivateKey());
            }
            if ("***".equals(config.getApiKey())) {
                config.setApiKey(existing.getApiKey());
            }
            
            paymentConfigMapper.updateById(config);
            return Result.success("更新支付配置成功", config);
        } catch (Exception e) {
            log.error("更新支付配置失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除支付配置
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deletePaymentConfig(@PathVariable Long id) {
        try {
            PaymentConfig config = paymentConfigMapper.selectById(id);
            if (config == null) {
                return Result.error("支付配置不存在");
            }
            
            paymentConfigMapper.deleteById(id);
            return Result.success("删除支付配置成功");
        } catch (Exception e) {
            log.error("删除支付配置失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 启用/禁用支付配置
     */
    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> togglePaymentConfig(@PathVariable Long id) {
        try {
            PaymentConfig config = paymentConfigMapper.selectById(id);
            if (config == null) {
                return Result.error("支付配置不存在");
            }
            
            config.setEnabled(config.getEnabled() == 1 ? 0 : 1);
            paymentConfigMapper.updateById(config);
            
            String status = config.getEnabled() == 1 ? "启用" : "禁用";
            return Result.success(status + "支付配置成功");
        } catch (Exception e) {
            log.error("切换支付配置状态失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}
