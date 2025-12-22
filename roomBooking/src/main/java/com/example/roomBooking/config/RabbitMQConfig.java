package com.example.roomBooking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "rooms-exchange";
    public static final String ROUTING_KEY_ROOM_BOOKED = "room.booked";
    public static final String ROUTING_KEY_ROOM_UNBOOKED = "room.unbooked";
    
    // Fanout exchange для RoomBookedEvent с ценой
    public static final String ROOM_BOOKED_FANOUT_EXCHANGE = "room-booked-fanout";
    public static final String ROOM_BOOKED_AUDIT_QUEUE = "room-booked-audit-queue";
    public static final String ROOM_BOOKED_LOGGING_QUEUE = "room-booked-logging-queue";

    @Bean
    public TopicExchange roomsExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
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
    public Queue roomBookedLoggingQueue() {
        return new Queue(ROOM_BOOKED_LOGGING_QUEUE, true);
    }
    
    @Bean
    public Binding roomBookedAuditBinding() {
        return BindingBuilder.bind(roomBookedAuditQueue()).to(roomBookedFanoutExchange());
    }
    
    @Bean
    public Binding roomBookedLoggingBinding() {
        return BindingBuilder.bind(roomBookedLoggingQueue()).to(roomBookedFanoutExchange());
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter(new ObjectMapper().findAndRegisterModules());
    }
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        // Устанавливаем callback для publisher confirms
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                System.out.println("NACK: Message delivery failed! " + cause);
            }
        });
        return rabbitTemplate;
    }

//    @Bean
//    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
//            ConnectionFactory connectionFactory) {
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        factory.setMessageConverter(messageConverter());
//        return factory;
//    }

}

