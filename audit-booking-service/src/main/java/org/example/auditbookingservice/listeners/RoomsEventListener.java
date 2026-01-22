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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomsEventListener {
    
    private static final Logger log = LoggerFactory.getLogger(RoomsEventListener.class);
    public static final String ROOM_BOOKED_FANOUT_EXCHANGE = "room-booked-fanout";
    public static final String EXCHANGE_NAME = "rooms-exchange";
    private static final String ROUTING_KEY_ROOM_UNBOOKED = "room.unbooked";
    private final InMemoryStorage storage;
    private final Set<Long> processedBookingIds = ConcurrentHashMap.newKeySet();

    public RoomsEventListener(InMemoryStorage storage) {
        this.storage = storage;
    }

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(
                name = "q.audit.analytics",
                durable = "true",
                arguments = {
                    @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                    @Argument(name = "x-dead-letter-routing-key", value = "dlq.notifications")
                }
            ),
            exchange = @Exchange(name = ROOM_BOOKED_FANOUT_EXCHANGE, type = "fanout")
        )
    )
    public void handleRoomBookedEvent(@Payload RoomBookedEvent event, Channel channel,
                                      @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            log.info("Received RoomBookedEvent: {}", event);

            if (!processedBookingIds.add(event.bookingId())) {
                log.warn("Duplicate event received for bookingId: {}", event.bookingId());
                channel.basicAck(deliveryTag, false);
                return;
            }

            if (event.documentNumber() == null || event.documentNumber().length() < 5) {
                throw new IllegalArgumentException(
                    "Номер документа содержит меньше 5 символов");
            }

            storage.bookRooms.put(event.bookingId(), event);
            log.info("Added new bookingEvent with bookingId: {}", event.bookingId());
            
            if (isForeigner(event.documentNumber())) {
                storage.foreignerBookings.put(event.bookingId(), event);
                log.warn("FOREIGNER BOOKING DETECTED | bookingId={}, roomId={}, hotelId={}, user={} {}, documentNumber={}, dateFrom={}, dateTo={}, bookedAt={}, price={} {}, fullEvent={}",
                    event.bookingId(), event.roomId(), event.hotelId(), event.userName(), event.userSurname(),
                    event.documentNumber(), event.from(), event.to(), event.bookedAt(), event.price(), event.currency(), event);
            }
            
            channel.basicAck(deliveryTag, false);
        } catch (Exception e){
            log.error("Failed to process event: {}. Sending to DLQ.", event, e);
            if (event != null && event.bookingId() != null) {
                processedBookingIds.remove(event.bookingId());
            }
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
            storage.foreignerBookings.remove(event.bookingId());
            processedBookingIds.remove(event.bookingId());
            log.info("Successfully removed booking with bookingId: {}", event.bookingId());
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Failed to process event: {}. Sending to DLQ.", event, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    private boolean isForeigner(String documentNumber) {
        if (documentNumber == null || documentNumber.isEmpty()) {
            return false;
        }
        return !documentNumber.matches("\\d{10}");
    }

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(name = "notification-queue.dlq", durable = "true"),
            exchange = @Exchange(name = "dlx-exchange", type = "topic", durable = "true"),
            key = "dlq.notifications"
        )
    )
    public void handleDlqMessages(@Payload Object failedMessage) {
        log.error("!!! Received message in DLQ: {}", failedMessage);
    }
}
