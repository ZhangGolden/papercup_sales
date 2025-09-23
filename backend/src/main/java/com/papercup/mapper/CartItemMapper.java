package com.papercup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.papercup.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 购物车Mapper接口
 */
@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {

    /**
     * 查询用户购物车列表（包含产品信息）
     */
    @Select("SELECT c.*, p.name as product_name, p.price as product_price, " +
            "p.images as product_images, p.stock as product_stock, " +
            "p.status as product_status " +
            "FROM cart_item c " +
            "LEFT JOIN product p ON c.product_id = p.id " +
            "WHERE c.user_id = #{userId} " +
            "ORDER BY c.created_time DESC")
    List<CartItem> selectCartItemsWithProduct(@Param("userId") Long userId);
}
