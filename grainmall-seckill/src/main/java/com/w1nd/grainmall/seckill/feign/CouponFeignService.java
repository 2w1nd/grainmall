package com.w1nd.grainmall.seckill.feign;

import com.w1nd.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("grainmall-coupon")
public interface CouponFeignService {

    @GetMapping("/coupon/seckillsession/lasts3DaySession")
    R getLasts3DaySession();
}