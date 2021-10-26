package com.w1nd.grainmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.w1nd.common.utils.PageUtils;
import com.w1nd.grainmall.product.entity.CategoryEntity;
import com.w1nd.grainmall.product.vo.Catalog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author w1nd
 * @email 584202045@qq.com
 * @date 2021-07-22 16:45:31
 */
public interface CategoryService extends IService<CategoryEntity> {
    /**
     * 找到catelogid的完整路径
     * 【父/子/孙】
     * @param catelogId
     * @return
     */
    Long[] findCatelogPath(Long catelogId);

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

    void updateCascade(CategoryEntity category);

    List<CategoryEntity> getLevel1Categorys();

    Map<String, List<Catalog2Vo>> getCatalogJson();
}

