package com.w1nd.grainmall.ware.vo;

import lombok.Data;

/**
 * @Description:
 * @author: w1nd
 * @date: 2021年09月15日 15:06
 */
@Data
public class PurchaseItemDoneVo {
    private Long itemId;
    private Integer status;
    private String reason;
}
