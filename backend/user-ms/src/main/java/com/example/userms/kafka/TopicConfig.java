package com.example.userms.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfig {
    @Bean
    public NewTopic firstTopic(){
        return TopicBuilder.name("confirm-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic secondTopic(){
        return TopicBuilder.name("set-psw-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic thirdTopic(){
        return TopicBuilder.name("email-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic fourthTopic(){
        return TopicBuilder.name("otp-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
