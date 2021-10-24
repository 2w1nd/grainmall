package com.w1nd.grainmall.product.feign;

import com.w1nd.common.to.SkuHasStockVo;
import com.w1nd.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("grainmall-ware")
public interface WareFeignService {
    @PostMapping("/ware/waresku/hasstock")
    R getSkusHasStock(@RequestBody List<Long> skuIds);
}
