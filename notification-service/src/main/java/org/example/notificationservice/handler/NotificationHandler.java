package org.example.notificationservice.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.events.RoomBookedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class NotificationHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationHandler.class);
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        logger.info("New WebSocket connection: {}. Total sessions: {}", session.getId(), sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        logger.info("WebSocket connection closed: {}. Total sessions: {}", session.getId(), sessions.size());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("WebSocket transport error for session: {}", session.getId(), exception);
        sessions.remove(session);
    }

    public void sendNotification(Object notification) {
        if (sessions.isEmpty()) {
            logger.warn("No active WebSocket sessions to send notification");
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(notification);
            TextMessage message = new TextMessage(json);
            
            List<WebSocketSession> closedSessions = new CopyOnWriteArrayList<>();
            
            for (WebSocketSession session : sessions) {
                try {
                    session.sendMessage(message);
                    logger.debug("Notification sent to session: {}", session.getId());
                } catch (Exception e) {
                    logger.error("Error sending message to session: {}", session.getId(), e);
                    closedSessions.add(session);
                }
            }
            
            sessions.removeAll(closedSessions);
            
            logger.info("Notification sent to {} active sessions", sessions.size());
        } catch (Exception e) {
            logger.error("Error serializing notification to JSON", e);
        }
    }

    public void handleRoomBookedEvent(RoomBookedEvent event) {
        logger.info("Processing RoomBookedEvent for notification: bookingId={}, roomId={}, price={} {}",
                event.bookingId(), event.roomId(), event.price(), event.currency());

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
        notification.put("message", String.format("Комната №%d забронирована. Цена: %.2f %s", 
                event.roomId(), event.price(), event.currency()));
        notification.put("timestamp", System.currentTimeMillis());

        sendNotification(notification);
        
        logger.info("Notification sent for roomId: {}. Active sessions: {}", 
                event.roomId(), getActiveSessionsCount());
    }

    public int getActiveSessionsCount() {
        return sessions.size();
    }
}

