package com.w1nd.grainmall.order.dao;

import com.w1nd.grainmall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author w1nd
 * @email 584202045@qq.com
 * @date 2021-07-22 20:51:54
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
