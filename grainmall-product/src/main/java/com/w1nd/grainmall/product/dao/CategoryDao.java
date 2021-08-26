package com.w1nd.grainmall.product.dao;

import com.w1nd.grainmall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author w1nd
 * @email 584202045@qq.com
 * @date 2021-07-22 16:45:31
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
