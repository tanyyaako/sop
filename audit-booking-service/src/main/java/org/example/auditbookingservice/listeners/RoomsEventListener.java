package org.example.auditbookingservice.listeners;

import com.rabbitmq.client.Channel;
import org.example.auditbookingservice.storage.InMemoryStorage;
import org.example.events.RoomBookedEvent;
import org.example.events.RoomUnbookedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class RoomsEventListener {
    
    private static final Logger log = LoggerFactory.getLogger(RoomsEventListener.class);
    public static final String EXCHANGE_NAME = "rooms-exchange";
    private static final String QUEUE_NAME = "notification-queue";
    private final InMemoryStorage storage;

    public RoomsEventListener(InMemoryStorage storage) {
        this.storage = storage;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = QUEUE_NAME,
                    durable = "true",
            arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                            @Argument(name = "x-dead-letter-routing-key", value = "dlq.notifications")
            }),
            exchange = @Exchange(name = EXCHANGE_NAME, type = "topic", durable = "true"),
            key = "room.booked"
    ))
    public void handleRoomBookedEvent(@Payload RoomBookedEvent event, Channel channel,
                                      @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            log.info("Received RoomBookedEvent: {}", event);
            RoomBookedEvent existingEvent = storage.bookRooms.get(event.bookingId());

            if (existingEvent != null) {
                RoomBookedEvent bookingEvent = new RoomBookedEvent(
                        event.bookingId(),
                        event.roomId(),
                        event.hotelId(),
                        event.documentNumber(),
                        event.userName(),
                        event.userSurname(),
                        event.from(),
                        event.to(),
                        null,
                        event.price(),
                        event.currency()
                );

                RoomBookedEvent existingWithoutTime = new RoomBookedEvent(
                        existingEvent.bookingId(),
                        existingEvent.roomId(),
                        existingEvent.hotelId(),
                        existingEvent.documentNumber(),
                        existingEvent.userName(),
                        existingEvent.userSurname(),
                        existingEvent.from(),
                        existingEvent.to(),
                        null,
                        existingEvent.price(),
                        existingEvent.currency()
                );

                if (event.userName().equals("1")) {
                    throw new RuntimeException("Duplicate RoomBookedEvent detected for bookingId: " + event.bookingId());
                }
            }

            storage.bookRooms.put(event.bookingId(), event);
            log.info("Added new bookingEvent with bookingId: {}", event.bookingId());
            channel.basicAck(deliveryTag, false);
        } catch (Exception e){
            log.error("Failed to process event: {}. Sending to DLQ.", event, e);
            channel.basicNack(deliveryTag, false, false);
        }

    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "notification-queue",
                    durable = "true",
                    arguments = {
                            @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                            @Argument(name = "x-dead-letter-routing-key", value = "dlq.notifications")
                    }),
            exchange = @Exchange(name = EXCHANGE_NAME, type = "topic", durable = "true"),
            key = "room.unbooked"
    ))
    public void handleRoomUnbookedEvent(@Payload RoomUnbookedEvent event, Channel channel,
                                        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            log.info("Received RoomUnbookedEvent: {}", event);
            storage.bookRooms.remove(event.bookingId());
            log.info("Successfully removed booking with bookingId: {}", event.bookingId());
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Failed to process event: {}. Sending to DLQ.", event, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "notification-queue.dlq", durable = "true"),
                    exchange = @Exchange(name = "dlx-exchange", type = "topic", durable = "true"),
                    key = "dlq.notifications"
            )
    )
    public void handleDlqMessages(Message failedMessage) {
        MessageProperties properties = failedMessage.getMessageProperties();

        String exceptionMessage = (String) properties.getHeader("x-exception-message");
        String stackTrace = (String) properties.getHeader("x-exception-stack-trace");
        String originalExchange = properties.getReceivedExchange();
        String originalRoutingKey = properties.getReceivedRoutingKey();
        List<Map<String, ?>> deathInfo = properties.getXDeathHeader();

        log.error("""
        !!! Received message in DLQ:
        Original exchange: {}
        Original routing key: {}
        Exception: {}
        Stack trace: {}
        Message body: {}
        Death info: {}
        """,
                originalExchange,
                originalRoutingKey,
                exceptionMessage,
                stackTrace,
                new String(failedMessage.getBody()),
                deathInfo
        );
    }
    
    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(name = "q.audit-booking-service.analytics", durable = "true"),
            exchange = @Exchange(name = "room-booked-fanout", type = "fanout")
        )
    )
    public void handleRoomBookedWithPrice(@Payload RoomBookedEvent event) {
        log.info("=== Room Booked Event with Price (Audit/Notification) ===");
        log.info("Booking ID: {}", event.bookingId());
        log.info("Room ID: {}", event.roomId());
        log.info("Hotel ID: {}", event.hotelId());
        log.info("User: {} {}", event.userName(), event.userSurname());
        log.info("Document: {}", event.documentNumber());
        log.info("Date From: {}", event.from());
        log.info("Date To: {}", event.to());
        log.info("Booked At: {}", event.bookedAt());
        log.info("Calculated Price: {} {}", event.price(), event.currency());
        log.info("==========================================================");
        
        // Здесь можно добавить отправку уведомления
        // notificationService.sendNotification("Room booked with price: " + event.price() + " " + event.currency());
    }
}
