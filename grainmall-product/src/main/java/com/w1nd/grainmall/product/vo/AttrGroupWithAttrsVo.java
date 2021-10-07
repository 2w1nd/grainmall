package com.w1nd.grainmall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.w1nd.grainmall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @author: w1nd
 * @date: 2021年08月30日 17:25
 */
@Data
public class AttrGroupWithAttrsVo {

    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;
}
