package com.example.commonsecurity.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public interface ApplicationSecurityConfigurer {
    void configure(HttpSecurity http) throws Exception;
}
