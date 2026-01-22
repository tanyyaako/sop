package com.example.roomBooking.listeners;

import com.example.roomBooking.config.RabbitMQConfig;
import org.example.events.RoomBookedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Argument;
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
            value = @Queue(
                name = "q.roomBooking.analytics.log",
                durable = "true",
                arguments = {
                    @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                    @Argument(name = "x-dead-letter-routing-key", value = "dlq.notifications")
                }
            ),
            exchange = @Exchange(name = RabbitMQConfig.ROOM_BOOKED_FANOUT_EXCHANGE, type = "fanout")
        )
    )
    public void handleRoomBookedWithPrice(RoomBookedEvent event) {
        logger.info("ROOM_BOOKED_EVENT | bookingId={}, roomId={}, hotelId={}, user={} {}, documentNumber={}, dateFrom={}, dateTo={}, bookedAt={}, price={} {}",
            event.bookingId(), event.roomId(), event.hotelId(), event.userName(), event.userSurname(),
            event.documentNumber(), event.from(), event.to(), event.bookedAt(), event.price(), event.currency());
    }
}

