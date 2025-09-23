package com.papercup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 订单请求DTO
 */
@Data
public class OrderRequest {

    /**
     * 购物车项ID列表
     */
    @NotEmpty(message = "购物车项不能为空")
    private List<Long> cartItemIds;

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    /**
     * 收货人电话
     */
    @NotBlank(message = "收货人电话不能为空")
    private String receiverPhone;

    /**
     * 省份
     */
    @NotBlank(message = "省份不能为空")
    private String province;

    /**
     * 城市
     */
    @NotBlank(message = "城市不能为空")
    private String city;

    /**
     * 区县
     */
    @NotBlank(message = "区县不能为空")
    private String district;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;

    /**
     * 邮政编码
     */
    private String postalCode;

    /**
     * 支付方式: ALIPAY, WECHAT, BANK
     */
    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 获取完整地址
     */
    public String getFullAddress() {
        return province + city + district + detailAddress;
    }
}
