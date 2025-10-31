package org.example.auditbookingservice.listeners;

import org.example.events.RoomBookedEvent;
import org.example.events.RoomUnbookedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RoomsEventListener {
    private static final Logger log = LoggerFactory.getLogger(RoomsEventListener.class);

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "notification-queue", durable = "true"),
            exchange = @Exchange(name = "rooms-exchange", type = "topic"),
            key = "room.booked"
    ))
    public void handleRoomBookedEvent(RoomBookedEvent event) {
        log.info("Received new room booking event: {}.", event);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "notification-queue", durable = "true"),
            exchange = @Exchange(name = "rooms-exchange", type = "topic"),
            key = "room.unbooked"
    ))
    public void handleRoomUnbookedEvent(RoomUnbookedEvent event) {
        log.info("Received new room unBooking event: {}.", event);
    }

}
