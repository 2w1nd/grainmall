package com.w1nd.grainmall.search;

import lombok.ToString;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
public class GrainmallSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrainmallSearchApplication.class, args);
    }

}
