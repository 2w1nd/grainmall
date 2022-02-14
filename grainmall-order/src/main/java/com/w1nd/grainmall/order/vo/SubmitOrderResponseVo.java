package com.w1nd.grainmall.order.vo;

import com.w1nd.grainmall.order.entity.OrderEntity;
import lombok.Data;

@Data
public class SubmitOrderResponseVo {

    private OrderEntity order;

    private Integer code;

}