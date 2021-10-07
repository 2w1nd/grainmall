package com.w1nd.grainmall.product.feign;

import com.w1nd.common.to.SkuReductionTo;
import com.w1nd.common.to.SpuBoundTo;
import com.w1nd.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient("grainmall-coupon")
public interface CounponFeignService {

    /**
     * 1. couponfeignService.saveSpuBounds
     *      1. @requestbody将这个对象转为json
     *      2. 找到grain-coupon远程服务, 给/coupon/spubounds/save发送请求，将上一步转的json放在请求体位置，发送请求
     *      3. 对方服务收到请求，请求体里有json数据
     *         请求体里的json转为spuboundsentity
     *  只要json数据模型是兼容的，双方服务无需使用同一个to
     * @param spuBoundTo
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
