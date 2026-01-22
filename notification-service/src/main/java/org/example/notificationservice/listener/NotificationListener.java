package org.example.notificationservice.listener;

import org.example.events.RoomBookedEvent;
import org.example.notificationservice.handler.NotificationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationListener.class);
    private final NotificationHandler notificationHandler;

    public NotificationListener(NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(
                name = "q.notification-service.analytics",
                durable = "true",
                arguments = {
                    @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                    @Argument(name = "x-dead-letter-routing-key", value = "dlq.notifications")
                }
            ),
            exchange = @Exchange(name = "room-booked-fanout", type = "fanout")
        )
    )
    public void handleRoomBookedEvent(@Payload RoomBookedEvent event) {
        logger.info("Received RoomBookedEvent from RabbitMQ: bookingId={}, roomId={}", 
                event.bookingId(), event.roomId());
        
        try {
            notificationHandler.handleRoomBookedEvent(event);
        } catch (Exception e) {
            logger.error("Error processing RoomBookedEvent: {}", event, e);
        }
    }
}

