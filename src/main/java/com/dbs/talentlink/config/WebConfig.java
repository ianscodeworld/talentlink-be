package com.dbs.talentlink.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 对所有 /api/ 开头的路径应用CORS配置
                .allowedOrigins("http://localhost:3000", "http://localhost:5173", "http://localhost:4200") // 允许的前端源地址，请根据你的实际情况修改
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的请求方法
                .allowedHeaders("*") // 允许所有的请求头
                .allowCredentials(true) // 是否允许携带cookie
                .maxAge(3600); // 预检请求的有效期，单位为秒
    }
}