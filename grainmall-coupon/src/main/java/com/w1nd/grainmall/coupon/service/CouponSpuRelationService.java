package com.w1nd.grainmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.w1nd.common.utils.PageUtils;
import com.w1nd.grainmall.coupon.entity.CouponSpuRelationEntity;

import java.util.Map;

/**
 * 优惠券与产品关联
 *
 * @author w1nd
 * @email 584202045@qq.com
 * @date 2021-07-22 20:32:20
 */
public interface CouponSpuRelationService extends IService<CouponSpuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

