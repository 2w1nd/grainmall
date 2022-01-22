package com.w1nd.grainmall.search.feign;

import com.w1nd.common.utils.R;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("grainmall-product")
public interface ProductFeignService {
    @GetMapping("/product/attr/info/{attrId}")
    public R attrsInfo(@PathVariable("attrId") Long attrId);

    @GetMapping("/product/brand/infos")
    public R brandsInfo(@RequestParam("brandIds") List<Long> brandIds);
}
