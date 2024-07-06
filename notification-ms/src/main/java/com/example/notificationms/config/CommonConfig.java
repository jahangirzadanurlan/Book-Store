package com.example.notificationms.config;

import com.example.commonemail.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
@RequiredArgsConstructor
@ComponentScan(basePackages = "com.example.commonemail.service")
public class CommonConfig {
    private final JavaMailSender javaMailSender;
    @Bean
    MailSenderService mailSenderService(){
        return new MailSenderService(javaMailSender);
    }

}
