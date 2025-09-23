package com.papercup.dto;

import lombok.Data;

/**
 * 产品查询请求DTO
 */
@Data
public class ProductQueryRequest {

    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 关键词搜索
     */
    private String keyword;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 状态: 0-下架, 1-上架
     */
    private Integer status;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方向: asc, desc
     */
    private String sortOrder;
}
