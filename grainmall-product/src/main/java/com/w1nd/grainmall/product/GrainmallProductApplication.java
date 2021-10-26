package com.w1nd.grainmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

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
 * 统一的异常处理
 * 1. 编写异常处理类，使用@ControllerAdvice
 * 2. 使用@ExceptionHandler标注方法可以处理的异常
 *
 * 模板引擎
 * 1. thymeleaf-starter：关闭缓存
 * 2. 静态资源都放在static文件夹下就可以按照路径访问
 * 3. 页面放在template下，直接访问
 */
@EnableFeignClients(basePackages = "com.w1nd.grainmall.product.feign")
@EnableDiscoveryClient
@MapperScan("com.w1nd.grainmall.product.dao")
@SpringBootApplication
public class GrainmallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrainmallProductApplication.class, args);
    }

}
