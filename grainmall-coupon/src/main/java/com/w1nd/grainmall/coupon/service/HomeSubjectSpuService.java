package com.w1nd.grainmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.w1nd.common.utils.PageUtils;
import com.w1nd.grainmall.coupon.entity.HomeSubjectSpuEntity;

import java.util.Map;

/**
 * 专题商品
 *
 * @author w1nd
 * @email 584202045@qq.com
 * @date 2021-07-22 20:32:20
 */
public interface HomeSubjectSpuService extends IService<HomeSubjectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

