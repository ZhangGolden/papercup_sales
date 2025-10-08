package com.papercup.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 支付配置实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("payment_config")
public class PaymentConfig {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 支付方式: WECHAT, ALIPAY
     */
    @TableField("payment_type")
    private String paymentType;

    /**
     * 应用ID
     */
    @TableField("app_id")
    private String appId;

    /**
     * 商户号
     */
    @TableField("merchant_id")
    private String merchantId;

    /**
     * 私钥
     */
    @TableField("private_key")
    private String privateKey;

    /**
     * 公钥证书
     */
    @TableField("public_key")
    private String publicKey;

    /**
     * API密钥
     */
    @TableField("api_key")
    private String apiKey;

    /**
     * 证书序列号（微信支付用）
     */
    @TableField("cert_serial_no")
    private String certSerialNo;

    /**
     * 支付回调地址
     */
    @TableField("notify_url")
    private String notifyUrl;

    /**
     * 支付成功跳转地址
     */
    @TableField("return_url")
    private String returnUrl;

    /**
     * 是否启用: 0-禁用, 1-启用
     */
    @TableField("enabled")
    private Integer enabled;

    /**
     * 是否沙箱环境: 0-生产, 1-沙箱
     */
    @TableField("sandbox")
    private Integer sandbox;

    /**
     * 配置描述
     */
    @TableField("description")
    private String description;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
