package com.w1nd.grainmall.member.feign;

import com.w1nd.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient("grainmall-order")
public interface OrderFeignService {

    @PostMapping("/order/order/listWithItem")
    public R listWithItem(@RequestBody Map<String, Object> params);
}