package com.w1nd.grainmall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class GrainmallThirdPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrainmallThirdPartyApplication.class, args);
    }

}
