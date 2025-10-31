package org.example.auditbookingservice.listeners;

import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RabbitMQConfig {

    @Bean
    public SimpleMessageConverter messageConverter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of(
                "org.example.events.*",
                "java.time.Ser",
                "java.time.*",
                "java.util.*"));
        return converter;
    }
}

