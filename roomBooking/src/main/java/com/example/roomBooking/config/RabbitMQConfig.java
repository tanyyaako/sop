package com.example.roomBooking.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "rooms-exchange";
    public static final String ROUTING_KEY_ROOM_BOOKED = "room.booked";
    public static final String ROUTING_KEY_ROOM_UNBOOKED = "room.unbooked";

    @Bean
    public TopicExchange roomsExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }
}

