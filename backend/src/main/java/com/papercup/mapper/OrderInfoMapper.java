package com.papercup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.papercup.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
}
