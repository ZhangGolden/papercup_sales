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
    List<CartItem> selectCartItemsWithProduct(@Param("userId") Long userId);
}
