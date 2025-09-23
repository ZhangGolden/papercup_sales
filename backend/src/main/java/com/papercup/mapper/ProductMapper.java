package com.papercup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.papercup.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 产品Mapper接口
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 分页查询产品列表（包含分类名称）
     */
    @Select("SELECT p.*, c.name as category_name " +
            "FROM product p " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "WHERE p.deleted = 0 " +
            "AND (#{keyword} IS NULL OR p.name LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND (#{categoryId} IS NULL OR p.category_id = #{categoryId}) " +
            "AND (#{status} IS NULL OR p.status = #{status}) " +
            "ORDER BY p.created_time DESC")
    IPage<Product> selectProductPage(Page<Product> page, 
                                     @Param("keyword") String keyword,
                                     @Param("categoryId") Long categoryId,
                                     @Param("status") Integer status);

    /**
     * 根据ID查询产品详情（包含分类名称）
     */
    @Select("SELECT p.*, c.name as category_name " +
            "FROM product p " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "WHERE p.id = #{id} AND p.deleted = 0")
    Product selectProductDetail(@Param("id") Long id);
}
