package com.papercup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.papercup.entity.PaymentOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 支付订单Mapper接口
 */
@Mapper
public interface PaymentOrderMapper extends BaseMapper<PaymentOrder> {

    /**
     * 根据支付订单号查询
     */
    @Select("SELECT * FROM payment_order WHERE payment_order_no = #{paymentOrderNo} AND deleted = 0")
    PaymentOrder selectByPaymentOrderNo(@Param("paymentOrderNo") String paymentOrderNo);

    /**
     * 根据业务订单号查询
     */
    @Select("SELECT * FROM payment_order WHERE business_order_no = #{businessOrderNo} AND deleted = 0")
    PaymentOrder selectByBusinessOrderNo(@Param("businessOrderNo") String businessOrderNo);
}
