package com.papercup.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品请求DTO
 */
@Data
public class ProductRequest {

    /**
     * 产品名称
     */
    @NotBlank(message = "产品名称不能为空")
    private String name;

    /**
     * 产品描述
     */
    private String description;

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /**
     * 价格
     */
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 库存数量
     */
    @NotNull(message = "库存数量不能为空")
    @Min(value = 0, message = "库存数量不能为负数")
    private Integer stock;

    /**
     * 产品图片JSON数组
     */
    private String images;

    /**
     * 规格信息JSON
     */
    private String specifications;

    /**
     * 是否可定制: 0-否, 1-是
     */
    private Integer customizable;

    /**
     * 定制选项JSON
     */
    private String customOptions;

    /**
     * 状态: 0-下架, 1-上架
     */
    private Integer status;
}
