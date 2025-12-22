package org.example.notificationservice.handler;

import org.example.events.RoomBookedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationHandler.class);
    private final NotificationWebSocketHandler webSocketHandler;

    public NotificationHandler(NotificationWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    public void handleRoomBookedEvent(RoomBookedEvent event) {
        logger.info("Processing RoomBookedEvent for notification: bookingId={}, roomId={}, price={} {}",
                event.bookingId(), event.roomId(), event.price(), event.currency());

        // Создаем JSON объект для уведомления
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "ROOM_BOOKED");
        notification.put("bookingId", event.bookingId());
        notification.put("roomId", event.roomId());
        notification.put("hotelId", event.hotelId());
        notification.put("userName", event.userName());
        notification.put("userSurname", event.userSurname());
        notification.put("dateFrom", event.from());
        notification.put("dateTo", event.to());
        notification.put("price", event.price());
        notification.put("currency", event.currency());
        notification.put("message", String.format("Комната #%d забронирована. Цена: %.2f %s", 
                event.roomId(), event.price(), event.currency()));
        notification.put("timestamp", System.currentTimeMillis());

        // Отправляем уведомление через WebSocket используя потокобезопасный список сессий
        webSocketHandler.sendNotification(notification);
        
        logger.info("Notification sent via WebSocket for roomId: {}. Active sessions: {}", 
                event.roomId(), webSocketHandler.getActiveSessionsCount());
    }
}

