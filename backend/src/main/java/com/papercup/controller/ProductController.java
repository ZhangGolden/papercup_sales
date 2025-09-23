package com.papercup.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.papercup.common.Result;
import com.papercup.dto.ProductQueryRequest;
import com.papercup.dto.ProductRequest;
import com.papercup.entity.Category;
import com.papercup.entity.Product;
import com.papercup.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品控制器
 */
@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 分页查询产品列表
     */
    @GetMapping("/list")
    public Result<IPage<Product>> getProductList(ProductQueryRequest request) {
        try {
            IPage<Product> page = productService.getProductPage(request);
            return Result.success(page);
        } catch (Exception e) {
            log.error("查询产品列表失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取产品详情
     */
    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            return Result.success(product);
        } catch (Exception e) {
            log.error("查询产品详情失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建产品 (管理员)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        try {
            Product product = productService.createProduct(request);
            return Result.success("创建产品成功", product);
        } catch (Exception e) {
            log.error("创建产品失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新产品 (管理员)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Product> updateProduct(@PathVariable Long id, 
                                        @Valid @RequestBody ProductRequest request) {
        try {
            Product product = productService.updateProduct(id, request);
            return Result.success("更新产品成功", product);
        } catch (Exception e) {
            log.error("更新产品失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除产品 (管理员)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return Result.<Void>success("删除产品成功");
        } catch (Exception e) {
            log.error("删除产品失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量删除产品 (管理员)
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteProducts(@RequestBody List<Long> ids) {
        try {
            productService.deleteProducts(ids);
            return Result.<Void>success("批量删除产品成功");
        } catch (Exception e) {
            log.error("批量删除产品失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新产品状态 (管理员)
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateProductStatus(@PathVariable Long id, 
                                           @RequestParam Integer status) {
        try {
            productService.updateProductStatus(id, status);
            return Result.<Void>success("更新产品状态成功");
        } catch (Exception e) {
            log.error("更新产品状态失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
