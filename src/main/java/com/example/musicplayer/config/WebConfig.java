package com.example.musicplayer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:*") // ✅ Cho mọi cổng local (hoặc cụ thể)
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true); // ✅ Bắt buộc dùng nếu frontend gửi kèm token/cookie
    }
}
