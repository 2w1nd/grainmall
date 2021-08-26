package com.w1nd.grainmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.w1nd.common.utils.PageUtils;
import com.w1nd.grainmall.coupon.entity.MemberPriceEntity;

import java.util.Map;

/**
 * 商品会员价格
 *
 * @author w1nd
 * @email 584202045@qq.com
 * @date 2021-07-22 20:32:20
 */
public interface MemberPriceService extends IService<MemberPriceEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

