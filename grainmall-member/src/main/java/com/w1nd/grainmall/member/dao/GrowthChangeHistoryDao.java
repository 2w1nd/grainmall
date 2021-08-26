package com.w1nd.grainmall.member.dao;

import com.w1nd.grainmall.member.entity.GrowthChangeHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 成长值变化历史记录
 * 
 * @author w1nd
 * @email 584202045@qq.com
 * @date 2021-07-22 20:43:19
 */
@Mapper
public interface GrowthChangeHistoryDao extends BaseMapper<GrowthChangeHistoryEntity> {
	
}
