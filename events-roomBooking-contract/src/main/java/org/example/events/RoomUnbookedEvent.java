package org.example.events;

import java.io.Serializable;
import java.time.LocalDateTime;

public record RoomUnbookedEvent(
        Long bookingId,
        LocalDateTime unbookedAt
) implements Serializable {
}
