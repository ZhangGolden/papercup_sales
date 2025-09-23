package com.papercup.controller;

import com.papercup.common.Result;
import com.papercup.entity.Category;
import com.papercup.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类控制器
 */
@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ProductService productService;

    /**
     * 获取所有分类列表
     */
    @GetMapping("/list")
    public Result<List<Category>> getCategoryList() {
        try {
            List<Category> categories = productService.getAllCategories();
            return Result.success(categories);
        } catch (Exception e) {
            log.error("查询分类列表失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
