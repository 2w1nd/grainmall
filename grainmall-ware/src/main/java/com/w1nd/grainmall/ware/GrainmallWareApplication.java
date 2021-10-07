package com.w1nd.grainmall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
public class GrainmallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrainmallWareApplication.class, args);
    }

}
