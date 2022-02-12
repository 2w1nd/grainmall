package com.w1nd.grainmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

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
 *
 * 整合redis
 * 1. 引入data-redis-starter
 * 2. 简单配置host等信息
 * 3. 使用springboot自动配置好的stringredistemplate来操作redis
 *
 * 整合redisson作为分布式锁等功能框架
 *  1. 引入依赖
 *  2. 撇脂redisson
 *
 *  整合springcache简化缓存开发
 *  1. 引入依赖
 *  2. 写配置
 *   1）自动配置了哪些
 *   2）配置使用redis作为缓存
 *  3. 测试使用缓存
 *  4. 原理
 *  CacheAutoConfiguration -> RedisCacheConfiguration ->
 *  自动配置了RedisCacheManager-> 初始化所有的缓存 -> 每个缓存决定使用什么配置
 *  -> 如果redisCacheConfiguration有就用已有的，没有就用默认配置
 *  -> 想改缓存的配置，只需要给容器放一个redisCacheConfiguration即可
 *  -> 就会应用到当前
 */
@EnableRedisHttpSession
@EnableCaching
@EnableFeignClients(basePackages = "com.w1nd.grainmall.product.feign")
@EnableDiscoveryClient
@MapperScan("com.w1nd.grainmall.product.dao")
@SpringBootApplication
public class GrainmallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrainmallProductApplication.class, args);
    }

}
