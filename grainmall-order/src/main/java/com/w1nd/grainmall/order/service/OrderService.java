package com.w1nd.grainmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.w1nd.common.utils.PageUtils;
import com.w1nd.grainmall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author w1nd
 * @email 584202045@qq.com
 * @date 2021-07-22 20:51:54
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

