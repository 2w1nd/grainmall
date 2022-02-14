package com.w1nd.grainmall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FareVo {
    //收货人地址信息
    private MemberAddressVo address;
    //费用
    private BigDecimal fare;
}
