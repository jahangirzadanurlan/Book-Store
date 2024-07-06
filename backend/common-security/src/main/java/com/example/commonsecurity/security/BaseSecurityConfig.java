package com.example.commonsecurity.security;

import com.example.commonsecurity.config.ApplicationSecurityConfigurer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class BaseSecurityConfig extends WebSecurityConfigurerAdapter {
    private final ApplicationSecurityConfigurer applicationSecurityConfigurer;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        applicationSecurityConfigurer.configure(http);

        http.cors().and()
                .csrf().disable()
                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeRequests().anyRequest().authenticated();
    }
}
