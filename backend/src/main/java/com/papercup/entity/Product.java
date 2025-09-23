package com.papercup.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产品实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("product")
public class Product {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 产品名称
     */
    @TableField("name")
    private String name;

    /**
     * 产品描述
     */
    @TableField("description")
    private String description;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 价格
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 原价
     */
    @TableField("original_price")
    private BigDecimal originalPrice;

    /**
     * 库存数量
     */
    @TableField("stock")
    private Integer stock;

    /**
     * 销量
     */
    @TableField("sales")
    private Integer sales;

    /**
     * 产品图片JSON数组
     */
    @TableField("images")
    private String images;

    /**
     * 规格信息JSON
     */
    @TableField("specifications")
    private String specifications;

    /**
     * 是否可定制: 0-否, 1-是
     */
    @TableField("customizable")
    private Integer customizable;

    /**
     * 定制选项JSON
     */
    @TableField("custom_options")
    private String customOptions;

    /**
     * 状态: 0-下架, 1-上架
     */
    @TableField("status")
    private Integer status;

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

    /**
     * 分类名称 (非数据库字段)
     */
    @TableField(exist = false)
    private String categoryName;
}
