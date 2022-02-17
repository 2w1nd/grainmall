package com.w1nd.grainmall.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 因为订单服务做了用户登录的拦截，所以远程调用订单服务需要用户信息，我们给它共享cookies
 */
@Configuration
public class GrainFeignConfig {

    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor(){
            @Override
            public void apply(RequestTemplate requestTemplate) {
                System.out.println("RequestInterceptor线程..."+Thread.currentThread().getId());
                //1、RequestContextHolder拿到刚进来的请求
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null){
                    HttpServletRequest request = attributes.getRequest();//老请求
                    if (request != null){
                        //同步请求头数据。Cookie
                        String cookie = request.getHeader("Cookie");
                        //给新请求同步了老请求的cookie
                        requestTemplate.header("Cookie",cookie);
                        System.out.println("feign远程之前先执行RequestInterceptor.apply()");
                    }
                }
            }
        };
    }
}