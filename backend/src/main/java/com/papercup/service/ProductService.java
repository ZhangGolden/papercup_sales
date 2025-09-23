package com.papercup.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.papercup.dto.ProductQueryRequest;
import com.papercup.dto.ProductRequest;
import com.papercup.entity.Category;
import com.papercup.entity.Product;
import com.papercup.mapper.CategoryMapper;
import com.papercup.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 产品服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    /**
     * 分页查询产品列表
     */
    public IPage<Product> getProductPage(ProductQueryRequest request) {
        Page<Product> page = new Page<>(request.getCurrent(), request.getSize());
        return productMapper.selectProductPage(page, request.getKeyword(), 
                                               request.getCategoryId(), request.getStatus());
    }

    /**
     * 根据ID获取产品详情
     */
    public Product getProductById(Long id) {
        Product product = productMapper.selectProductDetail(id);
        if (product == null) {
            throw new RuntimeException("产品不存在");
        }
        return product;
    }

    /**
     * 创建产品
     */
    @Transactional
    public Product createProduct(ProductRequest request) {
        // 验证分类是否存在
        Category category = categoryMapper.selectById(request.getCategoryId());
        if (category == null || category.getStatus() == 0) {
            throw new RuntimeException("分类不存在或已禁用");
        }

        // 创建产品
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategoryId(request.getCategoryId());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setStock(request.getStock());
        product.setImages(request.getImages());
        product.setSpecifications(request.getSpecifications());
        product.setCustomizable(request.getCustomizable() != null ? request.getCustomizable() : 0);
        product.setCustomOptions(request.getCustomOptions());
        product.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        product.setSales(0);

        productMapper.insert(product);

        log.info("创建产品成功: {}", product.getName());
        return getProductById(product.getId());
    }

    /**
     * 更新产品
     */
    @Transactional
    public Product updateProduct(Long id, ProductRequest request) {
        // 检查产品是否存在
        Product existingProduct = productMapper.selectById(id);
        if (existingProduct == null) {
            throw new RuntimeException("产品不存在");
        }

        // 验证分类是否存在
        Category category = categoryMapper.selectById(request.getCategoryId());
        if (category == null || category.getStatus() == 0) {
            throw new RuntimeException("分类不存在或已禁用");
        }

        // 更新产品信息
        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setCategoryId(request.getCategoryId());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setOriginalPrice(request.getOriginalPrice());
        existingProduct.setStock(request.getStock());
        existingProduct.setImages(request.getImages());
        existingProduct.setSpecifications(request.getSpecifications());
        existingProduct.setCustomizable(request.getCustomizable() != null ? request.getCustomizable() : 0);
        existingProduct.setCustomOptions(request.getCustomOptions());
        existingProduct.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        productMapper.updateById(existingProduct);

        log.info("更新产品成功: {}", existingProduct.getName());
        return getProductById(id);
    }

    /**
     * 删除产品
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("产品不存在");
        }

        productMapper.deleteById(id);
        log.info("删除产品成功: {}", product.getName());
    }

    /**
     * 批量删除产品
     */
    @Transactional
    public void deleteProducts(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择要删除的产品");
        }

        productMapper.deleteBatchIds(ids);
        log.info("批量删除产品成功，数量: {}", ids.size());
    }

    /**
     * 更新产品状态
     */
    @Transactional
    public void updateProductStatus(Long id, Integer status) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("产品不存在");
        }

        product.setStatus(status);
        productMapper.updateById(product);

        log.info("更新产品状态成功: {} -> {}", product.getName(), status == 1 ? "上架" : "下架");
    }

    /**
     * 获取所有分类列表
     */
    public List<Category> getAllCategories() {
        LambdaQueryWrapper<Category> query = new LambdaQueryWrapper<>();
        query.eq(Category::getStatus, 1)
             .orderByAsc(Category::getSortOrder)
             .orderByAsc(Category::getId);
        return categoryMapper.selectList(query);
    }
}
