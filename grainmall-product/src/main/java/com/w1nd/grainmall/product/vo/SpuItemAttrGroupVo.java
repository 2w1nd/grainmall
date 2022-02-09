package com.w1nd.grainmall.product.vo;

import com.w1nd.grainmall.product.vo.SaveVo.Attr;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class SpuItemAttrGroupVo {
    private String groupName;
    private List<Attr> attrs;
}
