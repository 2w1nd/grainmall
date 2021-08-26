package com.w1nd.grainmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @Description:
 * @author: w1nd
 * @date: 2021年07月30日 11:57
 */
@Configuration
public class GrainmallCorsConfiguration {
    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration();

//      1.配置跨域
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", corsConfiguration);
//        CorsConfigurationSource corsConfigurationSource = new CorsConfigurationSource() {
//            @Override
//            public org.springframework.web.cors.CorsConfiguration getCorsConfiguration(ServerWebExchange serverWebExchange) {
//                return null;
//            }
//        }
        return new CorsWebFilter(source);
    }
}
