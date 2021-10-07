package com.w1nd.grainmall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @author: w1nd
 * @date: 2021年09月08日 19:34
 */
@Data
public class MergeVo {
    private Long purchaseId;  // 整单id
    private List<Long> items; //
}
