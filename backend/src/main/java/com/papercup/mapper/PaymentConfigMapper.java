package com.papercup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.papercup.entity.PaymentConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 支付配置Mapper接口
 */
@Mapper
public interface PaymentConfigMapper extends BaseMapper<PaymentConfig> {

    /**
     * 根据支付方式获取启用的配置
     */
    @Select("SELECT * FROM payment_config WHERE payment_type = #{paymentType} AND enabled = 1 AND deleted = 0 LIMIT 1")
    PaymentConfig selectEnabledByPaymentType(@Param("paymentType") String paymentType);
}
