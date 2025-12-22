package org.example.events;

import java.io.Serializable;
import java.time.LocalDateTime;

public record RoomUnbookedEvent(
        Long bookingId,
        String unbookedAt
) implements Serializable {
}
