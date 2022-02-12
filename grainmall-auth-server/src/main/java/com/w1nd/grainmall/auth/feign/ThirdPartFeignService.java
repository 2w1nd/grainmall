package com.w1nd.grainmall.auth.feign;

import com.w1nd.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("grainmall-third-party")
public interface ThirdPartFeignService {
    @RequestMapping("/mail/sendcode")
    public R sendCode(@RequestParam("mail") String mail,@RequestParam("code") String code);
}
