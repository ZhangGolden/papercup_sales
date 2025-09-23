package com.papercup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.papercup.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户地址Mapper接口
 */
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {
}
