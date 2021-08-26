package com.w1nd.grainmall.product.vo;

import lombok.Data;

/**
 * @Description:
 * @author: w1nd
 * @date: 2021年08月26日 16:43
 */
@Data
public class AttrRespVO extends AttrVo {
    /**
     *  "catelogName": 所属分类名字  "手机/数码/手机"
     *  "groupName": 所属分组名字   "主体"
     */
    private String catelogName;
    private String groupName;

    private Long[] catelogPath;
}
