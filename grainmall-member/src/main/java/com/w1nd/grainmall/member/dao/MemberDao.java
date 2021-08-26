package com.w1nd.grainmall.member.dao;

import com.w1nd.grainmall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author w1nd
 * @email 584202045@qq.com
 * @date 2021-07-22 20:43:19
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
