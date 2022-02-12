package com.w1nd.grainmall.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GrainmallWebConfig implements WebMvcConfigurer {
        /**
         * 视图映射
         * @param registry
         */
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
                // registry.addViewController("/login.html").setViewName("login");
                registry.addViewController("/reg.html").setViewName("reg");
        }
}
