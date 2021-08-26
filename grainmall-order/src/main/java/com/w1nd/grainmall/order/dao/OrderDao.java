package com.w1nd.grainmall.order.dao;

import com.w1nd.grainmall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author w1nd
 * @email 584202045@qq.com
 * @date 2021-07-22 20:51:54
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
