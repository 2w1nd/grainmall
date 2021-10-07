package com.w1nd.grainmall.coupon.dao;

import com.w1nd.grainmall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author w1nd
 * @email 584202045@qq.com
 * @date 2021-09-02 15:50:31
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
