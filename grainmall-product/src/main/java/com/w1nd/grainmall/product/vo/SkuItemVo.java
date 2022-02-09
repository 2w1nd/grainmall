package com.w1nd.grainmall.product.vo;

import com.w1nd.grainmall.product.entity.SkuImagesEntity;
import com.w1nd.grainmall.product.entity.SkuInfoEntity;
import com.w1nd.grainmall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {
    // 1. sku基本信息获取 pms_sku_info
    SkuInfoEntity info;

    boolean hasStock = true;

    // 2. sku的图片信息 pms_sku_images
    List<SkuImagesEntity> images;
    // 3. 获取sku的销售属性组合
    List<SkuItemSaleAttrVo> saleAttr;
    // 4. 获取spu的介绍
    SpuInfoDescEntity desp;
    // 5. 获取spu的规格参数信息
    List<SpuItemAttrGroupVo> groupAttrs;

}
