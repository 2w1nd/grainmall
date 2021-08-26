package com.w1nd.grainmall.product.dao;

import com.w1nd.grainmall.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 * 
 * @author w1nd
 * @email 584202045@qq.com
 * @date 2021-07-22 17:10:04
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {
	
}
