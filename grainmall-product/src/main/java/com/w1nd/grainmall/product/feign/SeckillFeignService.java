package com.w1nd.grainmall.product.feign;

import com.w1nd.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient("grainmall-seckill")
public interface SeckillFeignService {
    @GetMapping(value = "/sku/seckill/{skuId}")
    @ResponseBody
    R getSkuSeckillInfo(@PathVariable("skuId") Long skuId);
}
