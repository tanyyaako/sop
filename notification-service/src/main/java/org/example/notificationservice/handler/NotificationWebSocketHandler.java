package org.example.notificationservice.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationWebSocketHandler.class);
    
    // Потокобезопасный список сессий
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

    /**
     * Отправляет уведомление всем подключенным клиентам
     */
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
                    if (session.isOpen()) {
                        session.sendMessage(message);
                        logger.debug("Notification sent to session: {}", session.getId());
                    } else {
                        closedSessions.add(session);
                    }
                } catch (Exception e) {
                    logger.error("Error sending message to session: {}", session.getId(), e);
                    closedSessions.add(session);
                }
            }
            
            // Удаляем закрытые сессии
            sessions.removeAll(closedSessions);
            
            logger.info("Notification sent to {} active sessions", sessions.size());
        } catch (Exception e) {
            logger.error("Error serializing notification to JSON", e);
        }
    }

    public int getActiveSessionsCount() {
        return sessions.size();
    }
}

