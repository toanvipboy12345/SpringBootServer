package com.ecommerce.Ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")  // Cho phép tất cả các endpoint
                        .allowedOriginPatterns("http://localhost:3000")  // Chỉ định rõ origin
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // Bao gồm PATCH và OPTIONS
                        .allowedHeaders("*")
                        .allowCredentials(true) // Cho phép gửi thông tin xác thực
                        .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"); // Thêm exposed headers
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/uploads/**")
                        .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/");
            }
        };
    }
}