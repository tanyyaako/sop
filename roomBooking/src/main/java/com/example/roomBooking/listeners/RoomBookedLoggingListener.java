package com.example.roomBooking.listeners;

import com.example.roomBooking.config.RabbitMQConfig;
import org.example.events.RoomBookedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RoomBookedLoggingListener {

    private static final Logger logger = LoggerFactory.getLogger(RoomBookedLoggingListener.class);

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(name = "q.roomBooking.analytics.log", durable = "true"),
            exchange = @Exchange(name = RabbitMQConfig.ROOM_BOOKED_FANOUT_EXCHANGE, type = "fanout")
        )
    )
    public void handleRoomBookedWithPrice(RoomBookedEvent event) {
        logger.info("=== Room Booked Event with Price (Logging) ===");
        logger.info("Booking ID: {}", event.bookingId());
        logger.info("Room ID: {}", event.roomId());
        logger.info("Hotel ID: {}", event.hotelId());
        logger.info("User: {} {}", event.userName(), event.userSurname());
        logger.info("Document: {}", event.documentNumber());
        logger.info("Date From: {}", event.from());
        logger.info("Date To: {}", event.to());
        logger.info("Booked At: {}", event.bookedAt());
        logger.info("Calculated Price: {} {}", event.price(), event.currency());
        logger.info("==============================================");
        
        // Здесь можно добавить кеширование цены, если нужно
        // cacheService.cachePrice(event.roomId(), event.price());
    }
}

