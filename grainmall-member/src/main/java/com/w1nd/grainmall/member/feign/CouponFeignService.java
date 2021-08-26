package com.w1nd.grainmall.member.feign;

import com.w1nd.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("grainmall-coupon")
public interface CouponFeignService {
    @RequestMapping("/coupon/couponspurelation/member/list")
    public R membercoupons();
}
