package org.example.auditbookingservice.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
    
    public static final String ROOM_BOOKED_FANOUT_EXCHANGE = "room-booked-fanout";
    public static final String ROOM_BOOKED_AUDIT_QUEUE = "room-booked-audit-queue";

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter(new ObjectMapper().findAndRegisterModules());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
    
    @Bean
    public FanoutExchange roomBookedFanoutExchange() {
        return new FanoutExchange(ROOM_BOOKED_FANOUT_EXCHANGE, true, false);
    }
    
    @Bean
    public Queue roomBookedAuditQueue() {
        return new Queue(ROOM_BOOKED_AUDIT_QUEUE, true);
    }
    
    @Bean
    public Binding roomBookedAuditBinding() {
        return BindingBuilder.bind(roomBookedAuditQueue()).to(roomBookedFanoutExchange());
    }

}

