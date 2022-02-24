package com.w1nd.grainmall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
public class GrainmallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrainmallCouponApplication.class, args);
    }

}
