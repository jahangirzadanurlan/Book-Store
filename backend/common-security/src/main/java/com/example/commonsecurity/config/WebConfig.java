package com.example.commonsecurity.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // İzin verilecek kökenler
                .allowedMethods("GET", "POST", "PUT", "DELETE") // İzin verilecek HTTP metotları
                .allowCredentials(true)
                .allowedHeaders("*"); // İzin verilecek başlıklar
    }
}