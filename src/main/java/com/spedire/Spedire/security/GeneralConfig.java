package com.spedire.Spedire.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class GeneralConfig implements WebMvcConfigurer {

    @Bean

    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8000", "https://52c5-102-88-69-102.ngrok-free.app ")
                        .allowedOrigins("*")
                        .allowedHeaders("*");
            }

        };
    }

}




