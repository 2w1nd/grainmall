package com.w1nd.grainmall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages="com.w1nd.grainmall.member.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class GrainmallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrainmallMemberApplication.class, args);
    }

}
