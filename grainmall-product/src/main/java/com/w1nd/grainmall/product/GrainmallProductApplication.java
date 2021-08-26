package com.w1nd.grainmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/***
 * 整合mybatis-plus
 * 导入依赖
 * 配置
 * 配置数据源
 * 1.导入数据库驱动
 * 2.在application.yml配置数据源相关信息
 * 配置mybatis-plus
 * 1.使用mapperscan
 * 2.告诉mybatis-plus，sql映射文件的位置
 *
 */
@EnableDiscoveryClient
@MapperScan("com.w1nd.grainmall.product.dao")
@SpringBootApplication
public class GrainmallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrainmallProductApplication.class, args);
    }

}
